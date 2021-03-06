create or replace PROCEDURE generate_pools (number_of_pools NUMBER) AS
pool_name VARCHAR2(4000);
number_of_tracks NUMBER;
town VARCHAR2(4000);
street VARCHAR2(4000);
pool_count NUMBER;
BEGIN
    SELECT COUNT(*) into pool_count FROM BASENY;
    FOR counter IN pool_count..number_of_pools+pool_count-1
    LOOP
        SELECT NAZWA into pool_name FROM
        ( SELECT NAZWA FROM GENERATOR_POOL_NAMES ORDER BY dbms_random.value )
        WHERE rownum = 1;

        number_of_tracks := dbms_random.value(3,6);

        SELECT MIASTO into town FROM
        ( SELECT MIASTO FROM GENERATOR_TOWNS ORDER BY dbms_random.value )
        WHERE rownum = 1;    

        SELECT ULICA into street FROM
        ( SELECT ULICA FROM GENERATOR_STREETS ORDER BY dbms_random.value )
        WHERE rownum = 1;    

        INSERT INTO BASENY
        VALUES (counter, pool_name, number_of_tracks, town, street);

    END LOOP;
END;