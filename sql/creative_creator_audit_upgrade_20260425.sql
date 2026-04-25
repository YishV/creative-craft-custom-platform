alter table creative_creator
  add column audit_status varchar(32) default 'pending' comment '审核状态（pending待审核 approved通过 rejected驳回）' after status,
  add column audit_remark varchar(500) default null comment '审核备注' after audit_status,
  add column audit_by varchar(64) default '' comment '审核人' after audit_remark,
  add column audit_time datetime default null comment '审核时间' after audit_by;

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
