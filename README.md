# MyBatisUtils

MyBatisUtils is a quick tool for generating mapping files. All you need to do is configure the database information to generate the Update and Insert statements for the table.**The tool supports SQL Server only for the time being**。

## Quick Start

Download the project, unzip and enter the MyBatisUtils directory. Edit the`config.properties` file to configure the basic information of the database:

```properties
#The IP address of the database
IP:192.168.124.31 
#Username
USERNAME:admin
#Pwssword
PASSWD:123
#Database name
DATABASE_NAME:test
#Table name
TABLE_NAME:t_emr_tracker_card_info
```

Open the terminal and execute the following command：

```shell
$ java -jar MyBatisUtils-0.1-beta.jar -type
```

Type has the following options:

- -f：Generate all the fields in the table.
- -u：Generate update statement.
- -i：Generate insert statement.

You will see the generated SQL statements：

```shell
$ java -jar MyBatisUtils-0.1-beta.jar -i
insert into system_auth
<trim prefix="(" suffix=")" suffixOverrides=",">
        <if test="ID!= null and ID!=''">
                ID,
        </if>
        <if test="AUTH_NAME!= null and AUTH_NAME!=''">
                AUTH_NAME,
        </if>
        <if test="HREF!= null and HREF!=''">
                HREF,
        </if>
</trim>
<trim prefix="values (" suffix=")" suffixOverrides=",">
        <if test="ID!= null and ID!=''">
                #{ID,jdbcType=NVARCHAR},
        </if>
        <if test="AUTH_NAME!= null and AUTH_NAME!=''">
                #{AUTH_NAME,jdbcType=NVARCHAR},
        </if>
        <if test="HREF!= null and HREF!=''">
                #{HREF,jdbcType=NVARCHAR},
        </if>
</trim>
```

