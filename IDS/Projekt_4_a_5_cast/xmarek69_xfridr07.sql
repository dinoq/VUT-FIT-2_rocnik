--VYPRACOVALI : XMAREK69 (PETR MAREK) a XFRIDR07 (JAN FRIDRICH)
-- IDS projekt : Kocici system

DROP TABLE Kocka CASCADE CONSTRAINTS;
DROP TABLE Zivot CASCADE CONSTRAINTS;
DROP TABLE Teritorium CASCADE CONSTRAINTS;
DROP TABLE Vec CASCADE CONSTRAINTS;
DROP TABLE Hostitel CASCADE CONSTRAINTS;
DROP TABLE Rasa CASCADE CONSTRAINTS;
DROP TABLE Divoka CASCADE CONSTRAINTS;
DROP TABLE Zdomacnena CASCADE CONSTRAINTS;
DROP TABLE Zije CASCADE CONSTRAINTS;
DROP TABLE Vlastni CASCADE CONSTRAINTS;
DROP TABLE Dominuje CASCADE CONSTRAINTS;
DROP TABLE Preferuje CASCADE CONSTRAINTS;
DROP TABLE Zapujceno CASCADE CONSTRAINTS;

DROP SEQUENCE ID_Kocky_SQ;
DROP SEQUENCE ID_teritoria_SQ;
DROP SEQUENCE ID_Veci_SQ;
DROP SEQUENCE ID_hostitele_SQ;

DROP PROCEDURE Napis_mista_zacatku_zivotu;
DROP PROCEDURE Hositelovi_veci_urcite_kocky;

CREATE TABLE Kocka 
(
  ID_kocky INT NOT NULL PRIMARY KEY,
  Hlavni_jmeno VARCHAR(100) NOT NULL,
  Vzorek_kuze VARCHAR(100) NOT NULL,
  Barva_srsti VARCHAR(50) NOT NULL,
  Rod VARCHAR(100) NOT NULL,
  Specificke_znameni VARCHAR(200) NOT NULL,
  Nazev_rasy VARCHAR(30) NOT NULL
);

CREATE SEQUENCE ID_Kocky_SQ;

CREATE OR REPLACE TRIGGER trigger_kocky
  BEFORE INSERT ON Kocka
  FOR EACH ROW
  WHEN (new.ID_kocky IS NULL)
BEGIN
  SELECT ID_Kocky_SQ.nextval
  INTO :new.ID_kocky
  FROM dual;
END;
/

CREATE TABLE Zivot 
(
  Cislo_zivota INT NOT NULL ,
  Zpusob_smrti VARCHAR(100) NOT NULL,
  Cas_narozeni DATE NOT NULL,
  Cas_umrti DATE NOT NULL,
  ID_kocky INT NOT NULL,
  ID_teritoria_konce INT NOT NULL,
  ID_teritoria_zacatku INT NOT NULL,
  CONSTRAINT zivot_pk PRIMARY KEY(ID_kocky, Cislo_zivota)

);

CREATE OR REPLACE TRIGGER trigger_zivota
  BEFORE INSERT ON Zivot
  FOR EACH ROW
BEGIN
    declare
    i INT := 0;
    BEGIN
        SELECT count(*) INTO i FROM Zivot WHERE Zivot.ID_kocky = :new.ID_kocky AND Zivot.Cislo_zivota+1 = :new.Cislo_zivota;
        IF (i = 0 AND :new.Cislo_zivota != 1) THEN
            BEGIN 
                RAISE_APPLICATION_ERROR(-20000,'Spatne cislovani zivota');
            END;
        END IF;
        IF (i>9) THEN
        BEGIN 
                RAISE_APPLICATION_ERROR(-20000,'Prekrocen maximalni pocet zivotu (9)');
            END;
        END IF;
    END;
END;
/
        
CREATE TABLE Teritorium 
(
  ID_teritoria INT NOT NULL PRIMARY KEY,
  Typ_teritoria VARCHAR(100) NOT NULL,
  Kapacita_teritoria INT NOT NULL
);
    
CREATE SEQUENCE ID_teritoria_SQ;

CREATE OR REPLACE TRIGGER trigger_teritoria
  BEFORE INSERT ON Teritorium
  FOR EACH ROW
  WHEN (new.ID_teritoria IS NULL)
BEGIN
  SELECT ID_teritoria_SQ.nextval
  INTO :new.ID_teritoria
  FROM dual;
END;
/ 

CREATE TABLE Vec 
(
  ID_Veci INT NOT NULL PRIMARY KEY,
  Typ_vlastnictvi VARCHAR(100) NOT NULL,
  Kvantita INT NOT NULL,
  Nazev_Veci VARCHAR(100) NOT NULL,
  ID_teritoria INT NOT NULL
);

CREATE SEQUENCE ID_Veci_SQ;

CREATE OR REPLACE TRIGGER trigger_veci
  BEFORE INSERT ON Vec
  FOR EACH ROW
  WHEN (new.ID_Veci IS NULL)
BEGIN
  SELECT ID_Veci_SQ.nextval
  INTO :new.ID_Veci
  FROM dual;
END;
/
        
CREATE TABLE Hostitel 
(
  ID_hostitele INT NOT NULL PRIMARY KEY,
  Jmeno VARCHAR(100) NOT NULL,
  Datum_narozeni DATE NOT NULL,
  Pohlavi VARCHAR(10) NOT NULL,
  ID_teritoria INT NOT NULL
);

CREATE SEQUENCE ID_hostitele_SQ;

CREATE OR REPLACE TRIGGER trigger_hostitele
  BEFORE INSERT ON Hostitel
  FOR EACH ROW
  WHEN (new.ID_hostitele IS NULL)
BEGIN
  SELECT ID_hostitele_SQ.nextval
  INTO :new.ID_hostitele
  FROM dual;
END;
/

CREATE TABLE Rasa 
(
  Nazev_rasy VARCHAR(30) NOT NULL PRIMARY KEY,
  Barvy_oci VARCHAR(30) NOT NULL,
  Puvod VARCHAR(100) NOT NULL,
  Maximalni_delka_tesaku VARCHAR(100) NOT NULL,  
  Dalsi_specificke_rysy VARCHAR(500) NOT NULL
);
------------GENERALIZACE_RASY------------------------------------
CREATE TABLE Divoka 
( 
  Nazev_rasy VARCHAR(30),
  CONSTRAINT FK_Nazev_rasy_divoke FOREIGN KEY (Nazev_rasy) REFERENCES Rasa(Nazev_rasy),
  Mira_nebezpeci VARCHAR(30) NOT NULL

);

CREATE TABLE Zdomacnena 
(
  Nazev_rasy VARCHAR(30),
  CONSTRAINT FK_Nazev_rasy_zdomancnene FOREIGN KEY (Nazev_rasy) REFERENCES Rasa(Nazev_rasy),
  Datum_ochoceni DATE NOT NULL
);

--------------------vztahy n ku n-----------------------------------------
CREATE TABLE Zije
(
  ID_kocky  INT NOT NULL,
  CONSTRAINT FK_Kocka_v_teritoriu FOREIGN KEY (ID_kocky) REFERENCES Kocka(ID_kocky),
  ID_teritoria INT NOT NULL,
  CONSTRAINT FK_Teritorium_kocky FOREIGN KEY (ID_teritoria) REFERENCES Teritorium(ID_teritoria)

);

CREATE TABLE Vlastni
(
  ID_kocky  INT NOT NULL,
  CONSTRAINT FK_Kocka_Vlastnici_Vec FOREIGN KEY (ID_kocky) REFERENCES Kocka(ID_kocky),
  ID_Veci INT NOT NULL,
  CONSTRAINT FK_Vec_kocky FOREIGN KEY (ID_Veci) REFERENCES Vec(ID_Veci),
  cas_privlastneni DATE NOT NULL,
  cas_odhozeni DATE NOT NULL
);

CREATE TABLE Dominuje
(
  ID_kocky INT NOT NULL,
  CONSTRAINT FK_Kocka_dominuje FOREIGN KEY (ID_kocky) REFERENCES Kocka(ID_kocky),
  ID_hostitele INT NOT NULL,
  CONSTRAINT FK_Hostitel_kocky FOREIGN KEY (ID_hostitele) REFERENCES Hostitel(ID_hostitele),
  Hostitel_nazyva_kocku VARCHAR(100) NOT NULL
);

CREATE TABLE Preferuje
(
  Nazev_rasy  VARCHAR(30) NOT NULL,
  CONSTRAINT FK_Preferovana_rasa FOREIGN KEY (Nazev_rasy) REFERENCES Rasa(Nazev_rasy),
  ID_hostitele INT NOT NULL,
  CONSTRAINT FK_Hostitel_preferujici_rasu FOREIGN KEY (ID_hostitele) REFERENCES Hostitel(ID_hostitele)
);

CREATE TABLE Zapujceno
(
  ID_hostitele  INT NOT NULL,
  CONSTRAINT FK_Zapujceno_hostiteli FOREIGN KEY (ID_hostitele) REFERENCES Hostitel(ID_hostitele),
  ID_Veci INT NOT NULL,
  CONSTRAINT FK_Zapujcena_Vec FOREIGN KEY (ID_Veci) REFERENCES Vec(ID_Veci),
  cas_privlastneni DATE NOT NULL,
  cas_vraceni DATE NOT NULL
);



--------------------------------------------------------------
-------------nastaveni cizich klicov------------------------
--------------------------------------------------------------

ALTER TABLE Kocka ADD CONSTRAINT FK_Rasa_kocky FOREIGN KEY (Nazev_rasy) REFERENCES Rasa(Nazev_rasy);
ALTER TABLE Zivot ADD CONSTRAINT FK_Kocici_Zivot FOREIGN KEY (ID_kocky) REFERENCES Kocka(ID_kocky);
ALTER TABLE Zivot ADD CONSTRAINT FK_Skoncil FOREIGN KEY (ID_teritoria_konce) REFERENCES Teritorium(ID_teritoria);
ALTER TABLE Zivot ADD CONSTRAINT FK_Zacal FOREIGN KEY (ID_teritoria_zacatku) REFERENCES Teritorium(ID_teritoria);
ALTER TABLE Hostitel ADD CONSTRAINT FK_Se_narodil_v FOREIGN KEY (ID_teritoria) REFERENCES Teritorium(ID_teritoria);
ALTER TABLE Vec ADD CONSTRAINT FK_patri_do FOREIGN KEY (ID_teritoria) REFERENCES Teritorium(ID_teritoria);

--vlozeni dat

INSERT INTO Rasa (Nazev_rasy, Barvy_oci, Puvod, Maximalni_delka_tesaku,Dalsi_specificke_rysy) VALUES ('Ocicat', 'modra', 'Dansko', '1 cm', 'male tlapky');
INSERT INTO Rasa (Nazev_rasy, Barvy_oci, Puvod, Maximalni_delka_tesaku,Dalsi_specificke_rysy) VALUES ('American Curl', 'zelena', 'Amerika', '1.2 cm', 'dlouhy ocas');
INSERT INTO Rasa (Nazev_rasy, Barvy_oci, Puvod, Maximalni_delka_tesaku,Dalsi_specificke_rysy) VALUES ('Sibirska', 'zelena', 'Rusko', '0.8 cm', 'silny dlouhy ocas');

INSERT INTO Kocka (Hlavni_jmeno, Vzorek_kuze, Barva_srsti, ROD, Specificke_znameni, Nazev_rasy) VALUES ('Mourek', 'flekata', 'hneda', 'Fridrichovci', 'velke oci', 'Ocicat');
INSERT INTO Kocka (Hlavni_jmeno, Vzorek_kuze, Barva_srsti, Rod, Specificke_znameni, Nazev_rasy) VALUES ('Tygr', 'flekata', 'hneda', 'Fridrichovci', 'kratky ocas', 'Ocicat');
INSERT INTO Kocka (Hlavni_jmeno, Vzorek_kuze, Barva_srsti, Rod, Specificke_znameni, Nazev_rasy) VALUES ('Macek', 'strakata', 'seda', 'Petrovci', 'bile tlapky', 'American Curl');
INSERT INTO Kocka (Hlavni_jmeno, Vzorek_kuze, Barva_srsti, Rod, Specificke_znameni, Nazev_rasy) VALUES ('Morris', 'strakata', 'seda', 'Petrovci', 'hunata srst', 'American Curl');
INSERT INTO Kocka (Hlavni_jmeno, Vzorek_kuze, Barva_srsti, Rod, Specificke_znameni, Nazev_rasy) VALUES ('Abraxas-Alexandreas', 'strakata', 'bilo-seda', 'Alexandrovci', 'male usi', 'Sibirska');

INSERT INTO Teritorium (Typ_teritoria, Kapacita_teritoria) VALUES ('Nemocnice', 200);
INSERT INTO Teritorium (Typ_teritoria, Kapacita_teritoria) VALUES ('Opustena zahrada', 200);
INSERT INTO Teritorium (Typ_teritoria, Kapacita_teritoria) VALUES ('Koupaliste', 300);
INSERT INTO Teritorium (Typ_teritoria, Kapacita_teritoria) VALUES ('Park', 500);
INSERT INTO Teritorium (Typ_teritoria, Kapacita_teritoria) VALUES ('Chata', 100);

INSERT INTO Hostitel (Jmeno, Datum_Narozeni, Pohlavi, ID_Teritoria) VALUES ('Frida', TO_DATE('02-05-1990','DD-MM-YYYY'), 'Muz', 1);
INSERT INTO Hostitel (Jmeno, Datum_Narozeni, Pohlavi, ID_Teritoria) VALUES ('Ondra', TO_DATE('09-02-1995','DD-MM-YYYY'), 'Muz', 5);
INSERT INTO Hostitel (Jmeno, Datum_Narozeni, Pohlavi, ID_Teritoria) VALUES ('Marek', TO_DATE('03-05-1997','DD-MM-YYYY'), 'Muz', 2);
INSERT INTO Hostitel (Jmeno, Datum_Narozeni, Pohlavi, ID_Teritoria) VALUES ('Honza', TO_DATE('15-03-1995','DD-MM-YYYY'), 'Muz', 3);
INSERT INTO Hostitel (Jmeno, Datum_Narozeni, Pohlavi, ID_Teritoria) VALUES ('Petr', TO_DATE('08-08-1998','DD-MM-YYYY'), 'Muz', 1);
INSERT INTO Hostitel (Jmeno, Datum_Narozeni, Pohlavi, ID_Teritoria) VALUES ('Jana', TO_DATE('05-04-1985','DD-MM-YYYY'), 'Zena', 4);

INSERT INTO Vec (Typ_vlastnictvi, Kvantita, Nazev_Veci, ID_teritoria) VALUES ('luzkovina', 2, 'Polstar', 1);
INSERT INTO Vec (Typ_vlastnictvi, Kvantita, Nazev_Veci, ID_teritoria) VALUES ('zabava', 1, 'Klubicko', 2);
INSERT INTO Vec (Typ_vlastnictvi, Kvantita, Nazev_Veci, ID_teritoria) VALUES ('zabava', 5, 'Balon', 4);

INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (1,'Prejeti autem', TO_DATE('05-08-2010','DD-MM-YYYY'), TO_DATE('12-09-2011','DD-MM-YYYY'), 1, 1, 1);
INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (1,'Smrt starim', TO_DATE('15-06-2011','DD-MM-YYYY'), TO_DATE('18-02-2013','DD-MM-YYYY'), 2, 2, 1);
INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (1,'Smrt starim', TO_DATE('01-02-1985','DD-MM-YYYY'), TO_DATE('02-08-1999','DD-MM-YYYY'), 5, 2, 1);
INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (2,'Utopena', TO_DATE('03-08-1999','DD-MM-YYYY'), TO_DATE('02-05-2010','DD-MM-YYYY'), 5, 3, 2);
INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (1,'Pad ze stromu', TO_DATE('04-06-1995','DD-MM-YYYY'), TO_DATE('04-06-2003','DD-MM-YYYY'), 3, 4, 2);
INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (1,'Uhorela', TO_DATE('05-04-2003','DD-MM-YYYY'), TO_DATE('22-08-2010','DD-MM-YYYY'), 4, 5, 2);
INSERT INTO Zivot (Cislo_zivota, Zpusob_smrti, Cas_narozeni, Cas_umrti, ID_kocky, ID_teritoria_konce, ID_teritoria_zacatku) VALUES (2,'Utopena', TO_DATE('05-06-2003','DD-MM-YYYY'), TO_DATE('30-07-2015','DD-MM-YYYY'), 3, 3, 2);

INSERT INTO Divoka (Nazev_rasy, Mira_nebezpeci) VALUES ('Ocicat','Mirne nebezpecna');

INSERT INTO Zdomacnena (Nazev_rasy, Datum_ochoceni) VALUES ('American Curl',TO_DATE('07-10-2005','DD-MM-YYYY'));
INSERT INTO Zdomacnena (Nazev_rasy, Datum_ochoceni) VALUES ('Sibirska',TO_DATE('01-02-1992','DD-MM-YYYY'));

INSERT INTO Zije (ID_kocky, ID_teritoria) VALUES (1, 1);
INSERT INTO Zije (ID_kocky, ID_teritoria) VALUES (2, 2);
INSERT INTO Zije (ID_kocky, ID_teritoria) VALUES (3, 4);
INSERT INTO Zije (ID_kocky, ID_teritoria) VALUES (4, 4);
INSERT INTO Zije (ID_kocky, ID_teritoria) VALUES (5, 2);

INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (3, 1, TO_DATE('07-10-2005','DD-MM-YYYY'), TO_DATE('07-10-2006','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (4, 2, TO_DATE('07-10-2006','DD-MM-YYYY'), TO_DATE('07-10-2008','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (5, 3, TO_DATE('02-12-2000','DD-MM-YYYY'), TO_DATE('04-03-2002','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (5, 2, TO_DATE('08-05-2003','DD-MM-YYYY'), TO_DATE('05-06-2003','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (2, 1, TO_DATE('25-03-2001','DD-MM-YYYY'), TO_DATE('29-08-2001','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (1, 2, TO_DATE('28-11-2001','DD-MM-YYYY'), TO_DATE('04-05-2003','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (5, 2, TO_DATE('13-12-1999','DD-MM-YYYY'), TO_DATE('20-12-1999','DD-MM-YYYY'));
INSERT INTO Vlastni (ID_kocky, ID_Veci, cas_privlastneni, cas_odhozeni) VALUES (5, 1, TO_DATE('22-12-1999','DD-MM-YYYY'), TO_DATE('20-02-2000','DD-MM-YYYY'));

INSERT INTO Dominuje (ID_kocky, ID_hostitele, Hostitel_nazyva_kocku) VALUES (1, 1, 'Kocourek');
INSERT INTO Dominuje (ID_kocky, ID_hostitele, Hostitel_nazyva_kocku) VALUES (2, 2, 'Kocour');
INSERT INTO Dominuje (ID_kocky, ID_hostitele, Hostitel_nazyva_kocku) VALUES (5, 3, 'Alex');
INSERT INTO Dominuje (ID_kocky, ID_hostitele, Hostitel_nazyva_kocku) VALUES (3, 4, 'Cica');
INSERT INTO Dominuje (ID_kocky, ID_hostitele, Hostitel_nazyva_kocku) VALUES (4, 6, 'Macik');

INSERT INTO Preferuje (Nazev_rasy, ID_hostitele) VALUES ('Ocicat', 1);
INSERT INTO Preferuje (Nazev_rasy, ID_hostitele) VALUES ('Sibirska', 2);
INSERT INTO Preferuje (Nazev_rasy, ID_hostitele) VALUES ('American Curl', 3);
INSERT INTO Preferuje (Nazev_rasy, ID_hostitele) VALUES ('Sibirska', 4);
INSERT INTO Preferuje (Nazev_rasy, ID_hostitele) VALUES ('Sibirska', 5);
INSERT INTO Preferuje (Nazev_rasy, ID_hostitele) VALUES ('Ocicat', 6);

INSERT INTO Zapujceno (ID_hostitele, ID_Veci, cas_privlastneni, cas_vraceni) VALUES (2, 1, TO_DATE('07-11-2005','DD-MM-YYYY'), TO_DATE('07-12-2005','DD-MM-YYYY'));
INSERT INTO Zapujceno (ID_hostitele, ID_Veci, cas_privlastneni, cas_vraceni) VALUES (3, 3, TO_DATE('02-12-2000','DD-MM-YYYY'), TO_DATE('03-12-2000','DD-MM-YYYY'));
INSERT INTO Zapujceno (ID_hostitele, ID_Veci, cas_privlastneni, cas_vraceni) VALUES (4, 2, TO_DATE('05-04-2003','DD-MM-YYYY'), TO_DATE('04-05-2003','DD-MM-YYYY'));
INSERT INTO Zapujceno (ID_hostitele, ID_Veci, cas_privlastneni, cas_vraceni) VALUES (6, 3, TO_DATE('01-11-2010','DD-MM-YYYY'), TO_DATE('05-11-2010','DD-MM-YYYY'));

--3. cast - prikazy SELECT

--1. Jake kocky maji zelene oci  - spojeni dvou tabulek
SELECT A.Hlavni_jmeno
FROM Kocka A, Rasa R
WHERE R.Barvy_oci='zelena' AND A.Nazev_rasy=R.Nazev_rasy;

--2. Jake veci jsou z teritoria park - spojeni dvou tabulek
SELECT V.Nazev_Veci
FROM Teritorium T, Vec V
WHERE T.ID_teritoria=V.ID_teritoria AND T.Typ_teritoria = 'Park';

--3. Kolikrat nejaka kocka vlastnila klubicko - spojeni tri tabulek, pouziti GROUP BY a agregacni funkci
SELECT A.ID_kocky,A.Hlavni_jmeno, COUNT(*) Pocet_vlastnictvi
FROM Kocka A, Vec V, Vlastni X
WHERE A.ID_kocky=X.ID_kocky AND V.ID_Veci=X.ID_Veci AND V.Nazev_Veci='Klubicko'
GROUP BY A.Hlavni_jmeno,A.ID_kocky,V.Nazev_Veci;

--4. Kolik hostitelu preferuje Sibirskou rasu - pouziti GROUP BY a agregacni funkci
SELECT P.Nazev_rasy, COUNT(*)Pocet_Hostitelu
FROM Hostitel H, Preferuje P
WHERE H.ID_hostitele= P.ID_hostitele AND P.Nazev_rasy='Sibirska'
GROUP BY P.Nazev_rasy;

--5. Ktere kocky vlastnili pouze veci z teritoria Nemocnice - pouziti predikatu EXISTS
SELECT A.Hlavni_jmeno
FROM Kocka A, Vec V, Vlastni X, Teritorium T
WHERE A.ID_kocky=X.ID_kocky AND V.ID_Veci=X.ID_Veci AND V.ID_teritoria=T.ID_teritoria AND T.Typ_teritoria = 'Nemocnice'
  AND NOT EXISTS
  (SELECT *
  FROM Teritorium T2,Vec V2,Vlastni X2
  WHERE A.ID_kocky=X2.ID_kocky AND V2.ID_Veci=X2.ID_Veci AND V2.ID_teritoria=T2.ID_teritoria AND T2.Typ_teritoria <> 'Nemocnice');

--6. Ktere kocky neumreli v teritoriu koupaliste - pouziti predikatu IN s vnorenym selectem
SELECT A.Hlavni_jmeno
FROM Kocka A
WHERE A.ID_kocky NOT IN
  (SELECT A.ID_kocky
  FROM Zivot Z, Teritorium T
  WHERE A.ID_kocky=Z.ID_kocky AND Z.ID_teritoria_konce=T.ID_teritoria AND T.Typ_teritoria = 'Koupaliste');


--procedury
--vypsani mista narozeni u vsech zivotu kocek
CREATE OR REPLACE PROCEDURE Napis_mista_zacatku_zivotu AS
    CURSOR kocky IS SELECT * FROM Kocka;
    Kocka kocky%ROWTYPE;
    CURSOR zivoty IS SELECT * FROM Zivot;
    Zivot zivoty%ROWTYPE; 
    CURSOR teritoria IS SELECT * FROM Teritorium;
    Teritorium teritoria%ROWTYPE; 
    BEGIN
        dbms_output.put_line('Seznam narozeni mist kocek');
        OPEN kocky;
        LOOP
            FETCH kocky INTO kocka;
            EXIT WHEN kocky%NOTFOUND;
            dbms_output.put_line(kocka.Hlavni_jmeno||':');
            OPEN zivoty;
            LOOP
                FETCH zivoty INTO zivot;
                EXIT WHEN zivoty%NOTFOUND;
                IF Kocka.ID_kocky=zivot.ID_kocky THEN
                    OPEN teritoria;
                    LOOP
                    FETCH teritoria INTO Teritorium;
                    EXIT WHEN teritoria%NOTFOUND;
                    
                      IF zivot.ID_teritoria_zacatku = Teritorium.ID_teritoria THEN
                        dbms_output.put_line('  '||Zivot.Cislo_zivota||'. '||Teritorium.Typ_teritoria);
                      END IF;
                    end LOOP;
                    CLOSE teritoria;
                END IF;
            END LOOP;
            CLOSE zivoty;
        END LOOP;
        CLOSE kocky;
        dbms_output.put_line('');
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20000,'Unexpected procudere error');
    END;
/
CREATE OR REPLACE PROCEDURE Hositelovi_veci_urcite_kocky (ID_kocky_arg NUMBER) AS
    CURSOR kocky IS SELECT * FROM Kocka;
    Kocka kocky%ROWTYPE;
    CURSOR hostitele IS SELECT * FROM Hostitel; /* JOIN Dominuje ON Hostitel.ID_hostitele = Dominuje.ID_hostitele;*/
    Hostitel hostitele%ROWTYPE; 
    cursor dominovani is select* from Dominuje;
    Dominuje dominovani%ROWTYPE; 
    CURSOR veci IS SELECT * FROM Vec JOIN Zapujceno ON Zapujceno.ID_Veci = Vec.ID_Veci;
    Vec veci%ROWTYPE; 
    BEGIN
        dbms_output.put_line('Seznam hostitelovych veci podle zadaneho id kocky');
        OPEN kocky;
        LOOP
            FETCH kocky INTO kocka;
            EXIT WHEN kocky%NOTFOUND;
            IF kocka.ID_kocky=ID_kocky_arg THEN
              dbms_output.put_line('veci hostitele kocky '||kocka.Hlavni_jmeno||':');
              OPEN dominovani;
              LOOP
                  FETCH dominovani INTO Dominuje;
                  EXIT WHEN dominovani%NOTFOUND;

                  IF Dominuje.ID_kocky=Kocka.ID_kocky THEN
                      open hostitele;

                      LOOP
                        FETCH hostitele INTO Hostitel;
                        EXIT WHEN hostitele%NOTFOUND;

                      IF Hostitel.ID_hostitele=Dominuje.ID_hostitele THEN
                        OPEN veci;
                        LOOP
                        FETCH veci INTO vec;
                        EXIT WHEN veci%NOTFOUND;
                      
                        IF vec.ID_hostitele = Hostitel.ID_hostitele THEN
                          dbms_output.put_line(' '||vec.Nazev_Veci);
                          END IF;
                        end LOOP;
                        CLOSE veci;
                    END IF;
                END LOOP;
                CLOSE hostitele;
                end IF;
              END LOOP;
              Close dominovani;
            END IF;
        END LOOP;
        CLOSE kocky;
        dbms_output.put_line('');
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20000,'Unexpected procudere error');
    END;
/
--zkouska procedur
SET SERVEROUTPUT ON;
BEGIN
    Napis_mista_zacatku_zivotu();
END;
/
BEGIN
    Hositelovi_veci_urcite_kocky(2);
END;
/
--ukazka prace s INDEX a EXPLAIN PLAN

EXPLAIN PLAN FOR
    SELECT R.Barvy_oci, COUNT(*) AS Pocet_kocek
    FROM Rasa R, Kocka A
    WHERE R.Nazev_rasy = A.Nazev_rasy
    GROUP BY R.Barvy_oci;
SELECT plan_table_output FROM table (dbms_xplan.display());

CREATE INDEX index_rasa ON Rasa(Barvy_oci,Nazev_rasy);

EXPLAIN PLAN FOR
    SELECT R.Barvy_oci, COUNT(*) AS Pocet_kocek
    FROM Rasa R, Kocka A
    WHERE R.Nazev_rasy = A.Nazev_rasy
    GROUP BY R.Barvy_oci;
SELECT plan_table_output FROM table (dbms_xplan.display());

DROP INDEX index_rasa;

/*--pro dokumentaci
SELECT R.Barvy_oci, COUNT(*) AS Pocet_kocek
FROM Rasa R, Kocka A
WHERE R.Nazev_rasy = A.Nazev_rasy
GROUP BY R.Barvy_oci;
*/


--definice pristupovych prav pro druheho clena
GRANT ALL ON  Rasa TO xfridr07;
GRANT ALL ON  Kocka TO xfridr07;
GRANT ALL ON  Zivot TO xfridr07;
GRANT ALL ON  Teritorium TO xfridr07;
GRANT ALL ON  Vec TO xfridr07;
GRANT ALL ON  Divoka TO xfridr07;
GRANT ALL ON  Zdomacnena TO xfridr07;
GRANT ALL ON  Zije TO xfridr07;
GRANT ALL ON  Vlastni TO xfridr07;
GRANT ALL ON  Dominuje TO xfridr07;
GRANT ALL ON  Hostitel TO xfridr07;
GRANT ALL ON  Preferuje TO xfridr07;
GRANT ALL ON  Zapujceno TO xfridr07;


--Log zmen 

CREATE MATERIALIZED VIEW LOG ON Kocka WITH PRIMARY KEY, ROWID;
CREATE MATERIALIZED VIEW LOG ON Rasa WITH PRIMARY KEY, ROWID;
--vytvoreni materializovaneho pohledu
CREATE MATERIALIZED VIEW Pridat_kocku
  NOLOGGING
  CACHE
  BUILD IMMEDIATE
  REFRESH FAST ON COMMIT
  ENABLE QUERY REWRITE
AS
SELECT Kocka.rowid as Kocka_rid,Rasa.rowid as Rasa_rid,Hlavni_jmeno,Vzorek_kuze,Barva_srsti,Rod,Specificke_znameni
FROM Kocka Join Rasa ON Kocka.Nazev_rasy=Rasa.Nazev_rasy;

GRANT ALL ON Pridat_kocku TO xfridr07;
--priklad pouziti:
--nejprve si vypiseme jak vypada to vypada pred upravami
SELECT Hlavni_jmeno,Vzorek_kuze,Barva_srsti,Rod,Specificke_znameni from Pridat_kocku;
INSERT INTO Kocka (Hlavni_jmeno, Vzorek_kuze, Barva_srsti, Rod, Specificke_znameni, Nazev_rasy) VALUES ('Petr', 'Pruhovana', 'hnedo-cerna', 'Lenivci', 'velke bricho, velice leniva', 'Ocicat');
--pote si vypisem jak to vypada po insertu - muzeme videt ze zadna zmena nenastala
SELECT Hlavni_jmeno,Vzorek_kuze,Barva_srsti,Rod,Specificke_znameni from Pridat_kocku;
--az po zadani prikazu COMMIT se zmeni hodnoty v tabulkach a provede se insert
COMMIT;
SELECT Hlavni_jmeno,Vzorek_kuze,Barva_srsti,Rod,Specificke_znameni from Pridat_kocku;

DROP MATERIALIZED VIEW Pridat_kocku;
