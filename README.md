\# SOAS PROJEKAT



\## 1. POKRETANJE PROJEKTA



Otvoriti CMD i pozicionirati se u glavni folder projekta:



cd C:\\Users\\Nevena\\Downloads\\Vezbe-master\\Vezbe-master





Servise pokretati jedan po jedan sledećim redosledom:



docker compose up -d --no-deps naming-server



docker compose up -d --no-deps users-service



docker compose up -d --no-deps currency-exchange



docker compose up -d --no-deps crypto-exchange



docker compose up -d --no-deps bank-account



docker compose up -d --no-deps crypto-wallet



docker compose up -d --no-deps currency-conversion



docker compose up -d --no-deps crypto-conversion



docker compose up -d --no-deps trade-service



docker compose up -d --no-deps api-gateway





Provera pokrenutih kontejnera:



docker compose ps





Eureka Naming Server:



http://localhost:8761





API Gateway:



http://localhost:8765







==================================================

2\. PORTOVI

==================================================



Naming Server:

8761



API Gateway:

8765



Users Service:

8770



Currency Exchange:

8000



Currency Conversion:

8100



Bank Account:

8200



Crypto Wallet:

8300



Crypto Exchange:

8400



Crypto Conversion:

8500



Trade Service:

8600







==================================================

3\. KORISNIČKI NALOZI

==================================================



OWNER



Email:

owner@uns.ac.rs



Password:

password





ADMIN



Email:

admin@uns.ac.rs



Password:

password





USER



Email:

user@uns.ac.rs



Password:

password





Za sve zahteve preko API Gateway-a koristi se:



Postman -> Authorization -> Basic Auth



Username = email korisnika

Password = password







==================================================

4\. H2 BAZA - VAŽNA NAPOMENA

==================================================



Projekat koristi H2 in-memory baze.



Nakon ponovnog kreiranja/pokretanja kontejnera podaci koji su ručno dodati mogu biti obrisani.



Osnovni korisnici:



owner@uns.ac.rs

admin@uns.ac.rs

user@uns.ac.rs



učitavaju se prilikom pokretanja Users Service-a.



Pre testiranja Currency Conversion, Crypto Conversion i Trade Service potrebno je napraviti/proveriti Bank Account i Crypto Wallet za USER korisnika i dodati mu sredstva.







==================================================

5\. PROVERA KORISNIKA

==================================================



METODA:

GET



URL:



http://localhost:8765/users



Basic Auth:



Username:

owner@uns.ac.rs



Password:

password





Treba da postoje:



owner@uns.ac.rs - OWNER

admin@uns.ac.rs - ADMIN

user@uns.ac.rs - USER







==================================================

6\. KREIRANJE NOVOG USER KORISNIKA

==================================================



NAPOMENA:

Ovo nije potrebno za osnovni user@uns.ac.rs ako je već učitan iz data.sql.

Koristi se za proveru automatskog kreiranja Bank Account-a i Crypto Wallet-a.



METODA:

POST



URL:



http://localhost:8765/users/newUser



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "testuser@uns.ac.rs",

&#x20;   "password": "password",

&#x20;   "role": "USER"

}



Dodavanjem USER korisnika automatski treba da se kreiraju njegov Bank Account i Crypto Wallet.







==================================================

7\. KREIRANJE ADMINA

==================================================



Samo OWNER može da kreira ADMIN korisnika.



METODA:

POST



URL:



http://localhost:8765/users/newAdmin



Basic Auth:



Username:

owner@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "admin2@uns.ac.rs",

&#x20;   "password": "password",

&#x20;   "role": "ADMIN"

}







==================================================

8\. KREIRANJE OWNERA

==================================================



U sistemu može postojati samo jedan OWNER.



Pokušaj kreiranja drugog OWNER-a treba da bude odbijen.



METODA:

POST



URL:



http://localhost:8765/users/newOwner



Basic Auth:



Username:

owner@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "owner2@uns.ac.rs",

&#x20;   "password": "password",

&#x20;   "role": "OWNER"

}







==================================================

9\. PRIPREMA BANK ACCOUNT-A ZA TESTIRANJE

==================================================



Ako user@uns.ac.rs nema Bank Account nakon ponovnog pokretanja H2 baze, napraviti ga.



METODA:

POST



URL:



http://localhost:8765/bank-accounts



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "user@uns.ac.rs"

}





Zatim dodati sredstva.





METODA:

PUT



URL:



http://localhost:8765/bank-accounts



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "user@uns.ac.rs",

&#x20;   "eur": 100,

&#x20;   "usd": 100,

&#x20;   "gbp": 100,

&#x20;   "chf": 100,

&#x20;   "rsd": 11700

}







==================================================

10\. PROVERA BANK ACCOUNT-A

==================================================



USER proverava svoj račun.



METODA:

GET



URL:



http://localhost:8765/bank-accounts/my-account



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





ADMIN može da vidi sve račune.



METODA:

GET



URL:



http://localhost:8765/bank-accounts



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password





ADMIN može da pronađe račun po email adresi.



METODA:

GET



URL:



http://localhost:8765/bank-accounts/email?email=user@uns.ac.rs



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password







==================================================

11\. PRIPREMA CRYPTO WALLET-A

==================================================



Ako user@uns.ac.rs nema Crypto Wallet nakon ponovnog pokretanja H2 baze, napraviti ga.



METODA:

POST



URL:



http://localhost:8765/crypto-wallets



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "user@uns.ac.rs"

}





Zatim dodati početna crypto sredstva.





METODA:

PUT



URL:



http://localhost:8765/crypto-wallets



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password



Body -> raw -> JSON:



{

&#x20;   "email": "user@uns.ac.rs",

&#x20;   "btc": 0.01,

&#x20;   "eth": 0.1,

&#x20;   "sol": 10

}







==================================================

12\. PROVERA CRYPTO WALLET-A

==================================================



USER proverava svoj wallet.



METODA:

GET



URL:



http://localhost:8765/crypto-wallets/my-wallet



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





ADMIN može da vidi sve wallet-e.



METODA:

GET



URL:



http://localhost:8765/crypto-wallets



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password





ADMIN može da pronađe wallet po email adresi.



METODA:

GET



URL:



http://localhost:8765/crypto-wallets/email?email=user@uns.ac.rs



Basic Auth:



Username:

admin@uns.ac.rs



Password:

password







==================================================

13\. CURRENCY EXCHANGE

==================================================



Currency Exchange vraća kurs između fiat valuta.



Dozvoljen je OWNER, ADMIN i USER korisnicima.



METODA:

GET



URL:



http://localhost:8765/currency-exchange?from=EUR\&to=RSD



Basic Auth može biti:



Username:

user@uns.ac.rs



Password:

password





Primer:



EUR -> RSD



exchangeRate = 117







==================================================

14\. CURRENCY CONVERSION

==================================================



Currency Conversion služi za stvarnu razmenu fiat valuta korisnika.



Samo USER ima pristup.



Primer:



10 EUR -> RSD





METODA:

GET



URL:



http://localhost:8765/currency-conversion?from=EUR\&to=RSD\&quantity=10



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Servis:



1\. Proverava stanje Bank Account-a.

2\. Uzima kurs od Currency Exchange servisa.

3\. Oduzima EUR.

4\. Dodaje RSD.

5\. Čuva novo stanje Bank Account-a.

6\. Vraća rezultat transakcije.





PROVERA NEDOVOLJNO SREDSTAVA:



METODA:

GET



URL:



http://localhost:8765/currency-conversion?from=EUR\&to=RSD\&quantity=999999



Basic Auth:



Username:

user@uns.ac.rs



Password:

password



Očekivana poruka:



User does not have enough money on bank account





PROVERA NEISPRAVNE KOLIČINE:



METODA:

GET



URL:



http://localhost:8765/currency-conversion?from=EUR\&to=RSD\&quantity=-10



Basic Auth:



Username:

user@uns.ac.rs



Password:

password



Očekuje se BAD\_REQUEST i poruka da quantity mora biti veći od nule.





ADMIN ne sme da koristi Currency Conversion.



Ako se isti zahtev pošalje kao:



admin@uns.ac.rs



treba da se dobije:



403 Forbidden







==================================================

15\. CRYPTO EXCHANGE

==================================================



Crypto Exchange vraća kurs između kriptovaluta.



Dozvoljen je OWNER, ADMIN i USER korisnicima.



METODA:

GET



URL:



http://localhost:8765/crypto-exchange?from=BTC\&to=ETH



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Primer kursa:



BTC -> ETH = 15







==================================================

16\. CRYPTO CONVERSION

==================================================



Crypto Conversion služi za razmenu jedne kriptovalute za drugu.



Samo USER može da izvrši transakciju.





Primer:



BTC -> ETH





METODA:

GET



URL:



http://localhost:8765/crypto-conversion?from=BTC\&to=ETH\&quantity=0.001



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Primer uspešnog rezultata:



Uspešno je izvršena razmena 0.001 BTC za 0.01500 ETH





PROVERA NEDOVOLJNO CRYPTO SREDSTAVA:



METODA:

GET



URL:



http://localhost:8765/crypto-conversion?from=BTC\&to=ETH\&quantity=999



Basic Auth:



Username:

user@uns.ac.rs



Password:

password



Očekivano:



User does not have enough crypto on wallet





ADMIN korisnik ne sme da izvršava Crypto Conversion.



Očekivani HTTP status:



403 Forbidden







==================================================

17\. TRADE SERVICE - FIAT U CRYPTO

==================================================



Trade Service omogućava kupovinu kriptovalute fiat valutom.



Samo USER ima pristup.





Primer:



10 EUR -> BTC





METODA:

GET



URL:



http://localhost:8765/trade-service?from=EUR\&to=BTC\&quantity=10



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Primer uspešnog rezultata:



10 EUR -> 0.00022222 BTC





Nakon transakcije:



\- EUR stanje se smanjuje

\- BTC stanje se povećava







==================================================

18\. TRADE SERVICE - CRYPTO U FIAT

==================================================



Primer:



0.001 BTC -> EUR





METODA:

GET



URL:



http://localhost:8765/trade-service?from=BTC\&to=EUR\&quantity=0.001



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Primer rezultata:



0.001 BTC -> 45 EUR





Nakon transakcije:



\- BTC stanje se smanjuje

\- EUR stanje se povećava







==================================================

19\. TRADE PREKO DRUGE FIAT VALUTE

==================================================



Trade Service podržava i fiat valute koje nisu direktno vezane za crypto kurs.



U tom slučaju konverzija ide preko EUR ili USD.





Primer:



RSD -> BTC





METODA:

GET



URL:



http://localhost:8765/trade-service?from=RSD\&to=BTC\&quantity=1170



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Primer uspešnog rezultata:



0.00026000 BTC za 1170 RSD preko EUR







==================================================

20\. TRADE - CRYPTO U RSD

==================================================



Primer:



BTC -> RSD





METODA:

GET



URL:



http://localhost:8765/trade-service?from=BTC\&to=RSD\&quantity=0.001



Basic Auth:



Username:

user@uns.ac.rs



Password:

password





Primer uspešnog rezultata:



5265.000000 RSD preko EUR







==================================================

21\. PROVERA AUTORIZACIJE

==================================================



API Gateway koristi Basic Authentication.



Ako se zahtev pošalje bez Basic Auth podataka očekuje se:



401 Unauthorized





USER nema pristup administraciji korisnika.



Na primer:



GET



http://localhost:8765/users



sa:



user@uns.ac.rs

password



treba da vrati:



403 Forbidden





ADMIN ne sme da kreira ADMIN korisnika.



POST



http://localhost:8765/users/newAdmin



sa ADMIN nalogom treba da vrati:



403 Forbidden





ADMIN ne sme da koristi Currency Conversion, Crypto Conversion ili Trade Service.



Očekivano:



403 Forbidden







==================================================

22\. AUTOMATSKO KREIRANJE BANK ACCOUNT-A I CRYPTO WALLET-A

==================================================



Kada se napravi novi USER:



POST



http://localhost:8765/users/newUser



Basic Auth:



admin@uns.ac.rs

password



Body:



{

&#x20;   "email": "testuser@uns.ac.rs",

&#x20;   "password": "password",

&#x20;   "role": "USER"

}





Automatski treba da se kreiraju:



Bank Account za:

testuser@uns.ac.rs



Crypto Wallet za:

testuser@uns.ac.rs





Provera Bank Account-a:



GET



http://localhost:8765/bank-accounts/email?email=testuser@uns.ac.rs



Basic Auth:



admin@uns.ac.rs

password





Provera Crypto Wallet-a:



GET



http://localhost:8765/crypto-wallets/email?email=testuser@uns.ac.rs



Basic Auth:



admin@uns.ac.rs

password







==================================================

23\. BRISANJE USER KORISNIKA

==================================================



OWNER može da obriše USER korisnika.



METODA:

DELETE



URL:



http://localhost:8765/users?email=testuser@uns.ac.rs



Basic Auth:



Username:

owner@uns.ac.rs



Password:

password





Brisanjem USER korisnika automatski treba da se obrišu:



\- njegov Bank Account

\- njegov Crypto Wallet





Posle brisanja proveriti:





BANK ACCOUNT:



GET



http://localhost:8765/bank-accounts/email?email=testuser@uns.ac.rs



Basic Auth:



admin@uns.ac.rs

password





CRYPTO WALLET:



GET



http://localhost:8765/crypto-wallets/email?email=testuser@uns.ac.rs



Basic Auth:



admin@uns.ac.rs

password





Korisnik više ne treba da postoji.







==================================================

24\. EUREKA NAMING SERVER

==================================================



Otvoriti u browseru:



http://localhost:8761





U Eureki treba proveriti da li su registrovani mikroservisi:



USERS-SERVICE



CURRENCY-EXCHANGE



CURRENCY-CONVERSION



BANK-ACCOUNT



CRYPTO-WALLET



CRYPTO-EXCHANGE



CRYPTO-CONVERSION



TRADE-SERVICE



API-GATEWAY







==================================================

25\. DOCKER

==================================================



Docker image-i su postavljeni na Docker Hub nalog:



nevenasaponja





Image-i:



nevenasaponja/naming-server:latest



nevenasaponja/users-service:latest



nevenasaponja/currency-exchange:latest



nevenasaponja/currency-conversion:latest



nevenasaponja/bank-account:latest



nevenasaponja/crypto-wallet:latest



nevenasaponja/crypto-exchange:latest



nevenasaponja/crypto-conversion:latest



nevenasaponja/trade-service:latest



nevenasaponja/api-gateway:latest







==================================================

26\. GLAVNI URL-OVI ZA ODBRANU

==================================================



Eureka:



http://localhost:8761





Users:



http://localhost:8765/users





Currency Exchange:



http://localhost:8765/currency-exchange?from=EUR\&to=RSD





Currency Conversion:



http://localhost:8765/currency-conversion?from=EUR\&to=RSD\&quantity=10





Bank Account:



http://localhost:8765/bank-accounts/my-account





Crypto Wallet:



http://localhost:8765/crypto-wallets/my-wallet





Crypto Exchange:



http://localhost:8765/crypto-exchange?from=BTC\&to=ETH





Crypto Conversion:



http://localhost:8765/crypto-conversion?from=BTC\&to=ETH\&quantity=0.001





Trade EUR -> BTC:



http://localhost:8765/trade-service?from=EUR\&to=BTC\&quantity=10





Trade BTC -> EUR:



http://localhost:8765/trade-service?from=BTC\&to=EUR\&quantity=0.001









