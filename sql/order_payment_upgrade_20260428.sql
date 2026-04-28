-- 订单交易演示字段升级脚本
-- 用途：为商品下单、购物车结算、模拟支付补充订单来源、支付状态和收货信息。

drop procedure if exists add_column_if_not_exists;

delimiter $$
create procedure add_column_if_not_exists(
    in table_name_param varchar(64),
    in column_name_param varchar(64),
    in column_definition text
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = table_name_param
          and column_name = column_name_param
    ) then
        set @sql = concat('alter table ', table_name_param, ' add column ', column_name_param, ' ', column_definition);
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
    end if;
end$$
delimiter ;

call add_column_if_not_exists('creative_custom_order', 'pay_status', "varchar(32) default 'unpaid' comment '支付状态（unpaid未支付 paid已支付）' after order_status");
call add_column_if_not_exists('creative_custom_order', 'source_type', "varchar(32) default null comment '订单来源类型' after pay_status");
call add_column_if_not_exists('creative_custom_order', 'source_id', "bigint(20) default null comment '订单来源ID' after source_type");
call add_column_if_not_exists('creative_custom_order', 'source_name', "varchar(128) default null comment '订单来源名称' after source_id");
call add_column_if_not_exists('creative_custom_order', 'quantity', "int(11) default 1 comment '购买数量' after source_name");
call add_column_if_not_exists('creative_custom_order', 'address_snapshot', "varchar(500) default null comment '收货信息快照' after quantity");

update creative_custom_order
set pay_status = 'unpaid'
where pay_status is null or pay_status = '';

drop procedure if exists add_column_if_not_exists;
