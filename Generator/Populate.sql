----========================================================================================================================----
----===-------------------------------------Wywolywanie procedur generujacych--------------------------------------------===----
----========================================================================================================================----
DECLARE
number_of_services NUMBER(3) := 0;
number_of_pools NUMBER(2) := 0;
BEGIN
generate_pools(5);
generate_inspections(10);
generate_posts;
generate_owner(0);
generate_auditor;
generate_workers(0,1,1,5,1,2,10,5);
generate_services;
generate_clients(971);

SELECT COUNT(*) INTO number_of_services FROM USLUGI;

SELECT COUNT(*) INTO number_of_pools FROM BASENY;

FOR counter IN 1..number_of_services
LOOP
    
    IF (counter>number_of_pools AND counter<=3*number_of_pools) THEN
	
        generate_transactions(counter, 5000, 2);
    
    ELSE 
    
        generate_transactions(counter, 500, 2);
        
    END IF;
	
END LOOP;

generate_lessons_reservations(500, 1000, 2);
END;
