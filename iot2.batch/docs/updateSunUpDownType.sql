-- insert into parameter(ID, PARAMETER_KEY, PARAMETER_VALUE, PARAMETER_TYPE, CONFIGURATION_ID) values('88896f0f-28b0-fa8b-0000-018bf328efd2', 'SunUpDownType', 'Mathematical', 'GlobalParameter', '00000000-0000-0001-0000-000000000001');
update  parameter set parameter_value = 'Civil' where parameter_key = 'SunUpDownType'

-- Mathematical, Civil, Nautical, Astronomical;

select * from parameter where parameter_key = 'SunUpDownType'

delete from parameter where parameter_key = 'SunUpDownType'