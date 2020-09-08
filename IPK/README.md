# IPK
IPK - Počítačové komunikace a sítě  
Projekt - Server poskytující informace o systému pomocí HTTP (v Javě) 
Projekt byl řešen samostatně.  
  
# Zadání projektu
Vypracujte síťovou aplikaci podle zadání.
Odevzdejte Váš projekt jako archív s názvem odpovídající Vašemu loginu. Projekty odevzdané po termínu nebuou hodnoceny. 
Odevzdáváte zdrojové soubory projektu - projekt může být napsán v jazyce C, Python nebo Java.  Projekt musí být spustitelný na referenčním stroji (merlin.fit.vutbr.cz)
Projekt musí obsahovat soubor Makefile pro přeložení make build (není-li potřebné jako prázdný target) a pro spuštění serveru make run pomocí make (gmake) (http://archiv.linuxsoft.cz/article.php?id_article=722)
Projekt musí být přeložitelný a spustitelný na referenčním stroji (merlin.fit.vutbr.cz)
Součástí projektu je také stručná dokumentace ve formátu plain text, Markdown, nebo PDF. 

# Popis varianty:
Úkolem je vytvoření serveru komunikujícího prostřednictvím protokolu HTTP, který bude poskytovat různé informace o systému. Server bude naslouchat na zadaném portu a podle url bude vracet požadované informace. Server musí správně zpracovávat hlavičky HTTP a vytvářet správné HTTP odpovědi. Typ odpovědi může být text/plain nebo application/json. Pro výběr typu odpovědi by měl server přihlížet k hlavičce Accept v dotazu. Komunikace se serverem by měla být možná jak pomocí webového prohlížeče, tak nástroji typu wget a curl.

Server bude spustitelný pomocí Makefile příkazem:

make run port=12345

kde 12345 je číslo portu na kterém server naslouchá. Zjistěte si, jak se používají takto definované argumenty v Makefile. Server bude možné ukončit pomocí CTRL+C. Server bude umět zpracovat následující tři typy dotazů, které jsou na server zaslané příkazem GET:


1. http://servername:12345/hostname
Vrací síťové jméno počítače včetně domény, například:

merlin.fit.vutbr.cz


2. http://servername:12345/cpu-name
Vrací informaci o procesoru, například:

Intel(R) Xeon(R) CPU E5-2640 0 @ 2.50GHz


3. http://servername:12345/load
Vrací aktuální informace o zátěži. Tento vyžaduje výpočet z hodnot uvedených v soubor /proc/stat (viz odkaz níže). Výsledek je například:

65%



Poslední příkaz (load) bude také mít nepovinnou volbu refresh=X, která zajistí, že prohlížeč si bude obnovovat informaci každých X vteřin. Například:
http://servername:12345/load?refresh=5



Potřebné informace pro odpověď lze v systému získat pomocí některých příkazů systému (uname, lscpu) a/nebo ze souborů v adresáři /proc. 



Pro implementaci serveru je nutné využít knihovny soketů. Není přípustné využívat knihovny pro zpracování HTTP a podobně - cílem je lightweight server, který má minimum závislostí. 


# HODNOCENÍ
Hodnotí se "kvalita" implementace (3/4 hodnocení), tedy:
splnění zadání a funkčnost implementace
jednoduchost kódu
množství závislostí na dalších knihovnách (rozumný balanc mezi tím co si napsat sám a co použít z knihoven)

Dále se pak hodnotí dokumentace (1/4) hodnocení.

# TESTOVÁNÍ
Vyzkoušejte si, zda Vám fungují alespoň základní varianty, tedy:

curl http://localhost:12345/hostname
curl http://localhost:12345/cpu-name
curl http://localhost:12345/load
curl http://localhost:12345/load?refresh=5

# DOPORUČENÍ
Zadání nepostihuje ani nemůže postihovat veškeré možnosti, které Vás napadnou při řešení nabo mohou nastat. V takovém případě je nutné navrhnou přijatelné a logické řešení. V případě, že si řešením nejste jisti se optejte prostřednictvím fóra.


# ODKAZY
Protokol HTTP RFC7231 - https://tools.ietf.org/html/rfc7231
HTTP pro vývojáře: https://developer.mozilla.org/en-US/docs/Web/HTTP
Výpočet CPU Load: https://stackoverflow.com/questions/23367857/accurate-calculation-of-cpu-usage-given-in-percentage-in-linux