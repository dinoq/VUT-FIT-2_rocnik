library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

entity ledc8x8 is
port ( 
	LED: out std_logic_vector (7 downto 0);--vystup pro LED
	ROW: out std_logic_vector (0 to 7);--vystup pro vyber radku
	SMCLK, RESET: in std_logic--vstupy pro hodinovy vstup a reset
);
end ledc8x8;


architecture main of ledc8x8 is
	signal sig_ROW : std_logic_vector (0 to 7) := "00000000"; --deklaruji si pomocny signal pro radek
	signal sig_LED : std_logic_vector (0 to 7) := "00000000"; --deklaruji si pomocny signal pro led
	signal citac : std_logic_vector (0 to 7) := "00000000"; --deklaruji si pomocny signal pro prepinani radku
	signal citac2 : std_logic_vector (0 to 20) := "000000000000000000000"; --deklaruji si pomocny signal pro rozdeleni sekundy na ctvrtiny
	signal stav : std_logic_vector (0 to 1) := "00"; --deklaruji si sihgnal pro stavy
	signal clock_enable : std_logic := '0'; --deklaruji si pomocny signal pro zmenu radku

begin
	--nasleduje delic kmitoctu pro spravne zobrazovani vsech radku za 1 sekundu a zaroven delic sekundy menici stav
	delic_kmitoctu: process (SMCLK, RESET) is
	begin
		if (RESET = '1') then--pokud doslo k resetu, vynuluj oba citace
			citac <= "00000000";
			citac2 <= "000000000000000000000";
		elsif (rising_edge(SMCLK)) then --jinak k oboum pricti 1
			citac <= citac + 1;
			citac2 <= citac2 + 1;
			
			if citac2 = "111000010000000000000" then--pokud uz uplynula ctvrtina sekundy, prepni stav a vynuluj citac2
				stav <= stav + 1;
				citac2 <= "000000000000000000000";
			end if;
		
			if citac = "11111111" then--pokud uz radek byl zapnuty 256 period SMCLK tak signalem clock_enable zmen radek a vynuluj citac 
				clock_enable <= '1';
				citac <= "00000000";
			else
				clock_enable <= '0'; --Jinak se nesmi zmenit radek
			end if;
			
		end if;
	
	end process delic_kmitoctu;


	--zmena radku
	dalsi_radek: process (clock_enable, RESET, SMCLK) is
	begin
		if RESET = '1' then--pokud dojde k resetu, nastav pocatecni radek
			sig_ROW <= "10000000";
		elsif (rising_edge(SMCLK)) then
			if  clock_enable = '1' then --pokud doslo k vzestupne hrane SMCLK a je umoznena zmena (signalem clock_enable), orotuj jednicku (zvol dalsi radek)
				sig_ROW <= sig_ROW(7) & sig_ROW(0 to 6);
			end if;
		end if;
	end process dalsi_radek;
	
	--dekoder pro jednotlive stavy, kde stav 00 je inicial jmena, stav 10 je inicial prijimeni a ostatní (stavy 01 a 11) jsou "mezistavy" nezobrazujici nic
	dekoder: process(sig_ROW, stav)
	begin
	if stav = 00 then
			case sig_ROW is
				when "00000001" => sig_LED <= "00001111";
				when "00000010" => sig_LED <= "01101111";
				when "00000100" => sig_LED <= "01101111";
				when "00001000" => sig_LED <= "00001111";
				when "00010000" => sig_LED <= "01111111";
				when "00100000" => sig_LED <= "01111111";
				when "01000000" => sig_LED <= "01111111";
				when "10000000" => sig_LED <= "01111111";
				when others => sig_LED <= "11111111";
			end case;
	elsif stav = 10 then	
			case sig_ROW is
				when "00000001" => sig_LED <= "01110111";
				when "00000010" => sig_LED <= "00100111";
				when "00000100" => sig_LED <= "01010111";
				when "00001000" => sig_LED <= "01110111";
				when "00010000" => sig_LED <= "01110111";
				when "00100000" => sig_LED <= "01110111";
				when "01000000" => sig_LED <= "01110111";
				when "10000000" => sig_LED <= "01110111";
				when others => sig_LED <= "11111111";
			end case;
	else
			case sig_ROW is
				when "00000001" => sig_LED <= "11111111";
				when others => sig_LED <= "11111111";
			end case;
	end if;
	end process dekoder;
	
	--Ulozime signal sig_ROW do ROW a sig_LED do LED
	ROW <= sig_ROW;
	LED <= sig_LED;
	
end main;
