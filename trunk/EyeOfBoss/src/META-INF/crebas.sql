/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2008-05-06 18:16:08                          */
/*==============================================================*/
/* Skrypt instalacyjny bazy danych*/

drop table if exists Grafik;

drop table if exists Pracownik;

drop table if exists Przyjscie_Wyjscie;

drop table if exists Sprzet;

drop table if exists Stawka;

drop table if exists Typy_zwolnien;

drop table if exists Wyplaty;

drop table if exists Zmiana;

drop table if exists Zwolnienia;

drop table if exists nadgodziny;

drop table if exists spoznienie;

drop table if exists uprawnienia;

drop table if exists uzytkownik;

drop table if exists wypozyczyl;

/*==============================================================*/
/* Table: Grafik                                                */
/*==============================================================*/
create table Grafik
(
   id_grafiku           int not null  AUTO_INCREMENT,
   id_zmiany            int,
   dzien_od             datetime,
   dzien_do             datetime,
   primary key (id_grafiku)
);

/*==============================================================*/
/* Table: Pracownik                                             */
/*==============================================================*/
create table Pracownik
(
   id_pracownika        int not null  AUTO_INCREMENT,
   id_stawki            int,
   id_zmiany            int,
   imie                 char(20),
   nazwisko             char(20),
   data_zatrudnienia    date,
   data_zwolnienia      date,
   miasto               text,
   adres                text,
   telefon              int,
   email                text,
   primary key (id_pracownika)
);

/*==============================================================*/
/* Table: Przyjscie_Wyjscie                                     */
/*==============================================================*/
create table Przyjscie_Wyjscie
(
   id_wewy              int not null  AUTO_INCREMENT,
   id_pracownika        int,
   godz_we              datetime,
   godz_wy              datetime,
   primary key (id_wewy)
);

/*==============================================================*/
/* Table: Sprzet                                                */
/*==============================================================*/
create table Sprzet
(
   id_sprzetu           int not null  AUTO_INCREMENT,
   opis                 text,
   cena                 decimal(6,2),
   primary key (id_sprzetu)
);

/*==============================================================*/
/* Table: Stawka                                                */
/*==============================================================*/
create table Stawka
(
   id_stawki            int not null  AUTO_INCREMENT,
   id_pracownika        int,
   dzienna              float(10),
   nadgodziny           float(10),
   weekendy             float(10),
   primary key (id_stawki)
);

/*==============================================================*/
/* Table: Typy_zwolnien                                         */
/*==============================================================*/
create table Typy_zwolnien
(
   id_typu              int not null AUTO_INCREMENT,
   nazwa_zwolnienia     text,
   mnoznik              float,
   primary key (id_typu)
);

/*==============================================================*/
/* Table: Wyplaty                                               */
/*==============================================================*/
create table Wyplaty
(
   id_wyplaty           int not null AUTO_INCREMENT,
   id_pracownika        int,
   kwota                float(10),
   data_od              date,
   za_co                text,
   primary key (id_wyplaty)
);

/*==============================================================*/
/* Table: Zmiana                                                */
/*==============================================================*/
create table Zmiana
(
   id_zmiany            int not null AUTO_INCREMENT,
   data_od              date,
   primary key (id_zmiany)
);

/*==============================================================*/
/* Table: Zwolnienia                                            */
/*==============================================================*/
create table Zwolnienia
(
   id_zwolnienia        int not null AUTO_INCREMENT,
   id_pracownika        int,
   id_typu              int,
   zwolnienie_od        date,
   zwolnienie_do        date,
   primary key (id_zwolnienia)
);

/*==============================================================*/
/* Table: nadgodziny                                            */
/*==============================================================*/
create table nadgodziny
(
   id_pracownika        int,
   id_nadgodzin         int not null AUTO_INCREMENT,
   data_wyjscia         datetime,
   czas                 time
);

/*==============================================================*/
/* Table: spoznienie                                            */
/*==============================================================*/
create table spoznienie
(
   id_spoznienia        int not null AUTO_INCREMENT,
   id_pracownika        int,
   data_przyjscia       int,
   czas_spoznienia      time,
   primary key (id_spoznienia)
);

/*==============================================================*/
/* Table: uprawnienia                                           */
/*==============================================================*/
create table uprawnienia
(
   id_uprawnienia       int not null AUTO_INCREMENT,
   uprawnienie          int,
   opis_uprawnienia     text,
   primary key (id_uprawnienia)
);

alter table uprawnienia comment 'Uprawnienia użytkowników';

/*==============================================================*/
/* Table: uzytkownik                                            */
/*==============================================================*/
create table uzytkownik
(
   id_uzytkownika       int not null AUTO_INCREMENT,
   id_pracownika        int,
   id_uprawnienia       int,
   login                text,
   password             int,
   primary key (id_uzytkownika)
);

/*==============================================================*/
/* Table: wypozyczyl                                            */
/*==============================================================*/
create table wypozyczyl
(
   id_pracownika        int not null,
   id_sprzetu           int not null,
   primary key (id_pracownika, id_sprzetu)
);

alter table Grafik add constraint FK_ma_grafik foreign key (id_zmiany)
      references Zmiana (id_zmiany) on delete restrict on update restrict;

alter table Pracownik add constraint FK_Jest_na foreign key (id_zmiany)
      references Zmiana (id_zmiany) on delete restrict on update restrict;

alter table Pracownik add constraint FK_posiada2 foreign key (id_stawki)
      references Stawka (id_stawki) on delete restrict on update restrict;

alter table Przyjscie_Wyjscie add constraint FK_przyszedł foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table Stawka add constraint FK_posiada foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table Wyplaty add constraint FK_otrzymal foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table Zwolnienia add constraint FK_Relationship_10 foreign key (id_typu)
      references Typy_zwolnien (id_typu) on delete restrict on update restrict;

alter table Zwolnienia add constraint FK_byl_na_zwolnieniu foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table nadgodziny add constraint FK_mial_nadgodz foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table spoznienie add constraint FK_spoznil_sie foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table uzytkownik add constraint FK_jest foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table uzytkownik add constraint FK_ma_uprawnienia foreign key (id_uprawnienia)
      references uprawnienia (id_uprawnienia) on delete restrict on update restrict;

alter table wypozyczyl add constraint FK_wypozyczyl foreign key (id_pracownika)
      references Pracownik (id_pracownika) on delete restrict on update restrict;

alter table wypozyczyl add constraint FK_wypozyczyl2 foreign key (id_sprzetu)
      references Sprzet (id_sprzetu) on delete restrict on update restrict;

