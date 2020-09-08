# IPS
IPS - Programovací seminář
Zadání úkolu 1:  

Cílem úkolu je implementovat funkce pro alokaci paměti.  
  
Pro hledání volných bloků použijte algoritmus Best fit.

Odevzdání
Odevzdávat bude každý student jeden soubor se jménem mmal.c do termínu Úloha 1. Pracovat můžete ve dvoučlenných týmech. Na prvním řádku v odevzdaném souboru bude v komentáři

// xlogin99
napsán login vašeho kolegy/kolegyně.
Materiál
Stáhněte si archiv se zdrojovými soubory mmal.zip. Obsah archivu:

Makefile - soubor s definicí překladu a spuštění testovacího případu.
mmal.h - hlavičkový soubor s prototypy funkcí pro alokaci a uvolnění paměti.
mmal.c - kostra zdrojového souboru s potřebnou funcionalitou (tento soubor budete odevzdávat).
test_mmal.c - zdrojový soubor pro test implementace.
Příkazem

$ make
spustíte překlad vaší implementace.
Základní test můžete spustit pomocí:

$ make test