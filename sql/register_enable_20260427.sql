-- 开启账号自助注册。已初始化过数据库时执行本脚本即可。

update sys_config
set config_value = 'true',
    update_by = 'admin',
    update_time = sysdate()
where config_key = 'sys.account.registerUser';
