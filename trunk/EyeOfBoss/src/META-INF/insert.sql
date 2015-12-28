#W tym skrypcie wpisujcie zapytania sql, które mają zostać wpisane do bazy danych
#Dzięki temu po zmianie bazy nie będzie trzeba znowu dopisywać danych.
#
insert into uzytkownik values(0,1,3,"luke",1234);
insert into uzytkownik values(1,2,0,"boss",1234);
insert into uzytkownik values(2,2,1,"admin",1234);
insert into uzytkownik values(3,2,2,"kadrowy",1234);

#zapytanie zwraca sumaryczny czas spóźnień 
    #select SEC_TO_TIME(sum(TIME_TO_SEC(czas_spoznienia))) from spoznienie
#zapytanie zwraca sumę przepracowanych godzin
    #SELECT sum(UNIX_TIMESTAMP(g.dzien_do)-UNIX_TIMESTAMP(g.dzien_od))
    #FROM Grafik g JOIN Zmiana z USING (id_zmiany)
    #WHERE z.id_zmiany=? AND MONTH(g.dzien_od)= ? AND YEAR(g.dzien_od)=?


insert uprawnienia values(0,0,"Szef");
insert uprawnienia values(1,1,"Administrator");
insert uprawnienia values(2,2,"Kadrowy");
insert uprawnienia values(3,3,"Pracownik");
insert into Pracownik values(2,0,0,"John","Q",CURDATE(),null,"New York", "Wall Street 1", 444555444,"jonq@gmail.com");
insert Typy_zwolnien values(0,"Urlop",1.0);
insert Typy_zwolnien values(1,"Urlop zdrowotny",0.9);
insert Typy_zwolnien values(2,"Urlop na zadanie",0);
insert Typy_zwolnien values(3,"Urlop okolicznosciowy",0.9);
insert into Sprzet values (1, "Oscyloskop", 1500, 'N');
insert into Sprzet values (2, "Monitor", 1000, 'N');
insert into Sprzet values (3, "Hulajnoga", 100, 'T');

drop table if exists Zwolnienia;
create table Zwolnienia
(
   id_zwolnienia        int not null AUTO_INCREMENT,
   id_pracownika        int,
   id_typu              int,
   zwolnienie_od        date,
   zwolnienie_do        date,
   primary key (id_zwolnienia)
);
alter table Zwolnienia add constraint FK_Relationship_10 foreign key (id_typu)
      references Typy_zwolnien (id_typu) on delete restrict on update restrict;
alter table Zwolnienia add constraint FK_byl_na_zwolnieniu foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;