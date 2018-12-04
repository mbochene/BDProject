CREATE TABLE audytor (
    numer_identyfikacyjny   INTEGER NOT NULL,
    id                      INTEGER NOT NULL
);

ALTER TABLE audytor ADD CONSTRAINT audytor_pk PRIMARY KEY ( numer_identyfikacyjny );

CREATE TABLE basen (
    numer_obiektu             INTEGER NOT NULL,
    nazwa_obiektu             VARCHAR2(4000) NOT NULL,
    liczba_torow_plywackich   INTEGER NOT NULL,
    miasto                    VARCHAR2(4000),
    ulica                     VARCHAR2(4000)
);

ALTER TABLE basen ADD CONSTRAINT basen_pk PRIMARY KEY ( numer_obiektu );

CREATE TABLE dane_do_logowania (
    id                      INTEGER NOT NULL,
    login                   VARCHAR2(4000) NOT NULL,
    haslo                   VARCHAR2(4000) NOT NULL,
    numer_identyfikacyjny   INTEGER
);

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT fkarc_1 CHECK ( ( ( numer_identyfikacyjny IS NOT NULL )
                                     AND ( numer_identyfikacyjny IS NULL )
                                     AND ( numer_identyfikacyjny IS NULL ) )
                                   OR ( ( numer_identyfikacyjny IS NOT NULL )
                                        AND ( numer_identyfikacyjny IS NULL )
                                        AND ( numer_identyfikacyjny IS NULL ) )
                                   OR ( ( numer_identyfikacyjny IS NOT NULL )
                                        AND ( numer_identyfikacyjny IS NULL )
                                        AND ( numer_identyfikacyjny IS NULL ) ) );

CREATE UNIQUE INDEX dane_do_logowania__idx ON
    dane_do_logowania (
        numer_identyfikacyjny
    ASC );

ALTER TABLE dane_do_logowania ADD CONSTRAINT dane_do_logowania_pk PRIMARY KEY ( id );

CREATE TABLE klient (
    numer_klienta    INTEGER NOT NULL,
    imie             VARCHAR2(4000) NOT NULL,
    nazwisko         VARCHAR2(4000) NOT NULL,
    numer_telefonu   VARCHAR2(4000) NOT NULL,
    "Adres_e-mail"   VARCHAR2(4000)
);

ALTER TABLE klient ADD CONSTRAINT klient_pk PRIMARY KEY ( numer_klienta );

CREATE TABLE koszyk (
    transakcja_numer_transakcji   INTEGER NOT NULL,
    numer_uslugi                  INTEGER NOT NULL,
    ilosc                         INTEGER NOT NULL
);

ALTER TABLE koszyk
    ADD CONSTRAINT fkarc_2 CHECK ( ( ( numer_uslugi IS NOT NULL )
                                     AND ( numer_uslugi IS NULL ) )
                                   OR ( ( numer_uslugi IS NOT NULL )
                                        AND ( numer_uslugi IS NULL ) ) );

ALTER TABLE koszyk ADD CONSTRAINT koszyk_pk PRIMARY KEY ( numer_uslugi,
                                                          transakcja_numer_transakcji );

CREATE TABLE lekcja_plywania (
    numer_lekcji             INTEGER NOT NULL,
    data                     DATE NOT NULL,
    godzina                  DATE NOT NULL,
    numer_ratownika          INTEGER NOT NULL,
    liczba_zapisanych_osob   INTEGER NOT NULL,
    klient_numer_klienta     INTEGER NOT NULL,
    ogolna_numer_uslugi      INTEGER NOT NULL
);

ALTER TABLE lekcja_plywania ADD CONSTRAINT lekcja_plywania_pk PRIMARY KEY ( numer_lekcji );

CREATE TABLE lokalna (
    numer_uslugi          INTEGER NOT NULL,
    nazwa_uslugi          VARCHAR2(4000) NOT NULL,
    cena                  INTEGER NOT NULL,
    basen_numer_obiektu   INTEGER NOT NULL
);

ALTER TABLE lokalna ADD CONSTRAINT lokalna_pk PRIMARY KEY ( numer_uslugi );

CREATE TABLE ogolna (
    numer_uslugi          INTEGER NOT NULL,
    nazwa_uslugi          VARCHAR2(4000) NOT NULL,
    cena                  INTEGER NOT NULL,
    basen_numer_obiektu   INTEGER NOT NULL
);

ALTER TABLE ogolna ADD CONSTRAINT ogolna_pk PRIMARY KEY ( numer_uslugi );

CREATE TABLE pracownik (
    numer_identyfikacyjny         INTEGER NOT NULL,
    imie                          VARCHAR2(4000) NOT NULL,
    nazwisko                      VARCHAR2(4000) NOT NULL,
    dodatek_do_pensji             INTEGER,
    stanowisko_numer_stanowiska   INTEGER NOT NULL,
    basen_numer_obiektu           INTEGER NOT NULL
);

ALTER TABLE pracownik ADD CONSTRAINT pracownik_pk PRIMARY KEY ( numer_identyfikacyjny );

CREATE TABLE przeglad (
    numer_przegladu       INTEGER NOT NULL,
    data                  DATE NOT NULL,
    basen_numer_obiektu   INTEGER NOT NULL
);

ALTER TABLE przeglad ADD CONSTRAINT przeglad_pk PRIMARY KEY ( numer_przegladu );

CREATE TABLE rezerwacja_toru (
    numer_rezerwacji       INTEGER NOT NULL,
    data                   DATE NOT NULL,
    godzina                DATE NOT NULL,
    numer_toru             INTEGER NOT NULL,
    status                 CHAR(1) NOT NULL,
    klient_numer_klienta   INTEGER NOT NULL,
    ogolna_numer_uslugi    INTEGER NOT NULL
);

ALTER TABLE rezerwacja_toru ADD CONSTRAINT rezerwacja_toru_pk PRIMARY KEY ( numer_rezerwacji );

CREATE TABLE stanowisko (
    numer_stanowiska   INTEGER NOT NULL,
    nazwa              VARCHAR2(4000) NOT NULL,
    wynagrodzenie      INTEGER NOT NULL
);

ALTER TABLE stanowisko ADD CONSTRAINT stanowisko_pk PRIMARY KEY ( numer_stanowiska );

CREATE TABLE transakcja (
    numer_transakcji   INTEGER NOT NULL,
    data               DATE NOT NULL
);

ALTER TABLE transakcja ADD CONSTRAINT transakcja_pk PRIMARY KEY ( numer_transakcji );

CREATE TABLE wlasciciel (
    numer_identyfikacyjny   INTEGER NOT NULL,
    id                      INTEGER NOT NULL,
    imie                    VARCHAR2(4000) NOT NULL,
    nazwisko                VARCHAR2(4000) NOT NULL
);

ALTER TABLE wlasciciel ADD CONSTRAINT wlasciciel_pk PRIMARY KEY ( numer_identyfikacyjny );

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT dane_audytor_fk FOREIGN KEY ( numer_identyfikacyjny )
        REFERENCES audytor ( numer_identyfikacyjny );

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT dane_pracownik_fk FOREIGN KEY ( numer_identyfikacyjny )
        REFERENCES pracownik ( numer_identyfikacyjny );

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT dane_wlasciciel_fk FOREIGN KEY ( numer_identyfikacyjny )
        REFERENCES wlasciciel ( numer_identyfikacyjny );

ALTER TABLE koszyk
    ADD CONSTRAINT koszyk_lokalna_fk FOREIGN KEY ( numer_uslugi )
        REFERENCES lokalna ( numer_uslugi );

ALTER TABLE koszyk
    ADD CONSTRAINT koszyk_ogolna_fk FOREIGN KEY ( numer_uslugi )
        REFERENCES ogolna ( numer_uslugi );

ALTER TABLE koszyk
    ADD CONSTRAINT koszyk_transakcja_fk FOREIGN KEY ( transakcja_numer_transakcji )
        REFERENCES transakcja ( numer_transakcji );

ALTER TABLE lekcja_plywania
    ADD CONSTRAINT lekcja_plywania_klient_fk FOREIGN KEY ( klient_numer_klienta )
        REFERENCES klient ( numer_klienta );

ALTER TABLE lekcja_plywania
    ADD CONSTRAINT lekcja_plywania_ogolna_fk FOREIGN KEY ( ogolna_numer_uslugi )
        REFERENCES ogolna ( numer_uslugi );

ALTER TABLE lokalna
    ADD CONSTRAINT lokalna_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE ogolna
    ADD CONSTRAINT ogolna_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE pracownik
    ADD CONSTRAINT pracownik_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE pracownik
    ADD CONSTRAINT pracownik_stanowisko_fk FOREIGN KEY ( stanowisko_numer_stanowiska )
        REFERENCES stanowisko ( numer_stanowiska );

ALTER TABLE przeglad
    ADD CONSTRAINT przeglad_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE rezerwacja_toru
    ADD CONSTRAINT rezerwacja_toru_klient_fk FOREIGN KEY ( klient_numer_klienta )
        REFERENCES klient ( numer_klienta );

ALTER TABLE rezerwacja_toru
    ADD CONSTRAINT rezerwacja_toru_ogolna_fk FOREIGN KEY ( ogolna_numer_uslugi )
        REFERENCES ogolna ( numer_uslugi );

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT dane_audytor_fk FOREIGN KEY ( numer_identyfikacyjny )
        REFERENCES audytor ( numer_identyfikacyjny );

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT dane_pracownik_fk FOREIGN KEY ( numer_identyfikacyjny )
        REFERENCES pracownik ( numer_identyfikacyjny );

ALTER TABLE dane_do_logowania
    ADD CONSTRAINT dane_wlasciciel_fk FOREIGN KEY ( numer_identyfikacyjny )
        REFERENCES wlasciciel ( numer_identyfikacyjny );

ALTER TABLE koszyk
    ADD CONSTRAINT koszyk_lokalna_fk FOREIGN KEY ( numer_uslugi )
        REFERENCES lokalna ( numer_uslugi );

ALTER TABLE koszyk
    ADD CONSTRAINT koszyk_ogolna_fk FOREIGN KEY ( numer_uslugi )
        REFERENCES ogolna ( numer_uslugi );

ALTER TABLE koszyk
    ADD CONSTRAINT koszyk_transakcja_fk FOREIGN KEY ( transakcja_numer_transakcji )
        REFERENCES transakcja ( numer_transakcji );

ALTER TABLE lekcja_plywania
    ADD CONSTRAINT lekcja_plywania_klient_fk FOREIGN KEY ( klient_numer_klienta )
        REFERENCES klient ( numer_klienta );

ALTER TABLE lekcja_plywania
    ADD CONSTRAINT lekcja_plywania_ogolna_fk FOREIGN KEY ( ogolna_numer_uslugi )
        REFERENCES ogolna ( numer_uslugi );

ALTER TABLE lokalna
    ADD CONSTRAINT lokalna_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE ogolna
    ADD CONSTRAINT ogolna_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE pracownik
    ADD CONSTRAINT pracownik_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE pracownik
    ADD CONSTRAINT pracownik_stanowisko_fk FOREIGN KEY ( stanowisko_numer_stanowiska )
        REFERENCES stanowisko ( numer_stanowiska );

ALTER TABLE przeglad
    ADD CONSTRAINT przeglad_basen_fk FOREIGN KEY ( basen_numer_obiektu )
        REFERENCES basen ( numer_obiektu );

ALTER TABLE rezerwacja_toru
    ADD CONSTRAINT rezerwacja_toru_klient_fk FOREIGN KEY ( klient_numer_klienta )
        REFERENCES klient ( numer_klienta );

ALTER TABLE rezerwacja_toru
    ADD CONSTRAINT rezerwacja_toru_ogolna_fk FOREIGN KEY ( ogolna_numer_uslugi )
        REFERENCES ogolna ( numer_uslugi );
