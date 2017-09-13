DROP TABLE IF EXISTS precomputeProduct CASCADE;
DROP TABLE IF EXISTS precomputeState CASCADE;
DROP TABLE IF EXISTS precomputeCell CASCADE;
DROP TABLE IF EXISTS log CASCADE;
DROP TRIGGER if exists insertTrigger ON products_in_cart;
DROP function if exists insertLog();

CREATE TABLE precomputeCell (
 id SERIAL PRIMARY KEY,
 state_id INTEGER REFERENCES state (id),
 product_id INTEGER REFERENCES product (id),
 totalSales BIGINT
);

CREATE TABLE precomputeProduct (
 id SERIAL PRIMARY KEY,
 product_id INTEGER REFERENCES product (id),
 totalSales BIGINT
);

CREATE TABLE precomputeState (
 id SERIAL PRIMARY KEY,
 state_id INTEGER REFERENCES state (id),
 category_id INTEGER,
 totalSales BIGINT
);

CREATE TABLE log (
 id SERIAL PRIMARY KEY,
 state_id INTEGER REFERENCES state (id),
 product_id INTEGER REFERENCES product (id),
 category_id INTEGER REFERENCES category(id),
 totalSales BIGINT NOT NULL
);

insert into precomputeProduct(product_id, totalSales)
select pr.id, coalesce(sum(pc.quantity*pc.price),0) from product pr
left join products_in_cart pc on pr.id = pc.product_id
left join shopping_cart sc on sc.is_purchased = true and sc.id = pc.cart_id
group by pr.id;


insert into precomputeState(state_id, category_id, totalSales)
select st.id, ca.id, coalesce(sum(pc.quantity * pc.price),0) from state st
left  join category ca on 1 = 1
left join product pr on pr.category_id = ca.id
left join person pe on st.id = pe.state_id
left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true
left join products_in_cart pc on pc.cart_id = sc.id and pr.id = pc.product_id
group by st.id, ca.id;

insert into precomputeCell(state_id, product_id, totalSales)
select st.id, pr.id, coalesce(sum(pc.quantity*pc.price),0)
from state st left join person pe on st.id = pe.state_id 
left join shopping_cart sc on sc.is_purchased=true and sc.person_id = pe.id 
left join products_in_cart pc on sc.id = pc.cart_id 
inner join product pr on pc.product_id = pr.id
group by st.id, pr.id;

insert into precomputeState(state_id, category_id, totalSales)
select st.id, 0, coalesce(sum(pc.quantity * pc.price),0) from state st
left join person pe on st.id = pe.state_id
left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true
left join products_in_cart pc on pc.cart_id = sc.id 
left join product pr on pr.id = pc.product_id
group by st.id;

create function insertLog() returns trigger as $insertLog$
begin
insert into log(state_id, product_id, category_id, totalSales) 
select st.id, pr.id, ca.id, coalesce(sum(new.quantity*new.price),0)
from state st join person pe on st.id = pe.state_id 
join shopping_cart sc on sc.person_id = pe.id and sc.id = new.cart_id 
join product pr on new.product_id = pr.id
join category ca on pr.category_id = ca.id
group by st.id, ca.id, pr.id;
RETURN NULL;
END;
$insertLog$ LANGUAGE plpgsql;

create trigger insertTrigger after insert
on products_in_cart
for each row
execute procedure insertLog();

--state_topK_allCategory
select st.id, st.state_name, coalesce(ps.totalSales,0) from state st, precomputeState ps
where st.id = ps.state_id and ps.category_id = 0
order by totalSales desc nulls last;
--state_topK_category
select st.id, st.state_name, coalesce(ps.totalSales,0) from state st, precomputeState ps
where st.id = ps.state_id and ps.category_id = ?
order by totalSales desc nulls last;
--product_topK_allCategory
select pr.id, pr.product_name, coalesce(pp.totalSales,0) from product pr, precomputeProduct pp
where pr.id = pp.product_id
order by totalSales desc nulls last limit 50;
--product_topK_category
select pr.id, pr.product_name, coalesce(pp.totalSales,0) from product pr, precomputeProduct pp
where pr.id = pp.product_id and pr.category_id = ?
order by totalSales desc nulls last limit 50;
--tate_product_topK_category
select pcc.totalSales from precomputeCell pcc
where pcc.state_id = ? and pcc.product_id = ?; 

select * from log;

update precomputeProduct pp set totalSales = pp.totalSales + t.totalSales
from ( select log.product_id, coalesce(sum(log.totalSales),0) as totalSales
    FROM log log  
    GROUP BY log.product_id) t  
WHERE pp.product_id = t.product_id;

update precomputeState ps set totalSales = ps.totalSales + t.totalSales
from ( select log.state_id, log.category_id, coalesce(sum(log.totalSales),0) as totalSales
    FROM log log  
    GROUP BY log.state_id, log.category_id) t  
where t.state_id = ps.state_id and t.category_id = ps.category_id;

update precomputeState ps set totalSales = ps.totalSales + t.totalSales
from ( select log.state_id, coalesce(sum(log.totalSales),0) as totalSales
    FROM log log  
    GROUP BY log.state_id) t
where t.state_id = ps.state_id and 0 = ps.category_id;

--check_cell_exists
select distinct pcc.state_id, pcc.product_id
from log log, precomputeCell pcc
where log.state_id=pcc.state_id and log.product_id=pcc.product_id;

--check_cell_notexists
select t.state_id, t.product_id, coalesce (sum(l.totalSales),0)
from (select distinct log.state_id, log.product_id, pcc.totalSales as totalSales from log log left join precomputeCell pcc
on log.state_id=pcc.state_id and log.product_id=pcc.product_id) t, log l
where t.totalSales is null and l.state_id=t.state_id and l.product_id=t.product_id 
group by t.state_id, t.product_id;



update precomputeCell pcc set totalSales = pcc.totalSales + t.totalSales
from ( select log.state_id, log.product_id, coalesce(sum(log.totalSales),0) as totalSales
    FROM log log  
    GROUP BY log.state_id, log.product_id) t
where t.state_id = pcc.state_id and t.product_id = pcc.product_id and pcc.state_id=? and pcc.product_id=?;


--red_product_sum
 select totalSales from precomputeProduct where product_id = ?;
    
--red_state_sum
 select totalSales from precomputeState where state_id = ?;