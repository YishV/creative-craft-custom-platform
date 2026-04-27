-- 创作者认证审核增量脚本
-- 可重复执行：字段存在时跳过，creator 角色存在时跳过。

drop procedure if exists add_creative_creator_audit_column;

delimiter $$
create procedure add_creative_creator_audit_column(
  in column_name_value varchar(64),
  in column_definition_value varchar(1000)
)
begin
  if not exists (
    select 1
    from information_schema.columns
    where table_schema = database()
      and table_name = 'creative_creator'
      and column_name = column_name_value
  ) then
    set @ddl = concat('alter table creative_creator add column ', column_definition_value);
    prepare stmt from @ddl;
    execute stmt;
    deallocate prepare stmt;
  end if;
end$$
delimiter ;

call add_creative_creator_audit_column(
  'audit_status',
  'audit_status varchar(32) default ''pending'' comment ''审核状态（pending待审核 approved通过 rejected驳回）'' after status'
);

call add_creative_creator_audit_column(
  'audit_remark',
  'audit_remark varchar(500) default null comment ''审核备注'' after audit_status'
);

call add_creative_creator_audit_column(
  'audit_by',
  'audit_by varchar(64) default '''' comment ''审核人'' after audit_remark'
);

call add_creative_creator_audit_column(
  'audit_time',
  'audit_time datetime default null comment ''审核时间'' after audit_by'
);

drop procedure if exists add_creative_creator_audit_column;

update creative_creator
set audit_status = 'approved'
where audit_status is null;

insert into sys_role (
  role_id,
  role_name,
  role_key,
  role_sort,
  data_scope,
  menu_check_strictly,
  dept_check_strictly,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
select
  3,
  '创作者',
  'creator',
  3,
  '2',
  1,
  1,
  '0',
  '0',
  'admin',
  sysdate(),
  '',
  null,
  '创作者角色'
from dual
where not exists (
  select 1 from sys_role where role_key = 'creator' and del_flag = '0'
);
