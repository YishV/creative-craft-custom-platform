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
  4,
  '买家角色',
  'buyer',
  3,
  2,
  1,
  1,
  '0',
  '0',
  'admin',
  sysdate(),
  '',
  null,
  '买家角色'
from dual
where not exists (
  select 1 from sys_role where role_key = 'buyer' and del_flag = '0'
);

insert into sys_role_menu (role_id, menu_id)
select buyer.role_id, rm.menu_id
from sys_role buyer
join sys_role common_role on common_role.role_key = 'common' and common_role.del_flag = '0'
join sys_role_menu rm on rm.role_id = common_role.role_id
left join sys_role_menu existing on existing.role_id = buyer.role_id and existing.menu_id = rm.menu_id
where buyer.role_key = 'buyer'
  and buyer.del_flag = '0'
  and existing.role_id is null;

insert into sys_role_dept (role_id, dept_id)
select buyer.role_id, rd.dept_id
from sys_role buyer
join sys_role common_role on common_role.role_key = 'common' and common_role.del_flag = '0'
join sys_role_dept rd on rd.role_id = common_role.role_id
left join sys_role_dept existing on existing.role_id = buyer.role_id and existing.dept_id = rd.dept_id
where buyer.role_key = 'buyer'
  and buyer.del_flag = '0'
  and existing.role_id is null;

insert into sys_user_role (user_id, role_id)
select ur.user_id, buyer.role_id
from sys_user_role ur
join sys_role common_role on common_role.role_id = ur.role_id and common_role.role_key = 'common' and common_role.del_flag = '0'
join sys_role buyer on buyer.role_key = 'buyer' and buyer.del_flag = '0'
left join sys_user_role existing on existing.user_id = ur.user_id and existing.role_id = buyer.role_id
where existing.user_id is null;
