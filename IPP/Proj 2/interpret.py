#!/usr/bin/python3

import sys
import re
import xml.etree.ElementTree as ET
import InterpretConstants

label_dict = {}
IP = 0
IP_stack = []

date_stack = []

input_is_set = True
source_is_set = True

input_f = None

class XMLChecker:
	#def __init__(self):


	def check(self, source):
		try:
			xml = ET.parse(source)
		except:
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_FORMAT)+"): chyba v xml souboru!\n")
			sys.exit(InterpretConstants.XML_FORMAT)

		root = xml.getroot()

		if(root.tag != "program"):
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(root.attrib["language"] != "IPPcode19"):
			sys.exit(InterpretConstants.XML_STRUCTURE)

		for attrib in root.attrib:
			if (attrib != "language" and attrib != "name" and attrib !="description"):
				sys.exit(InterpretConstants.XML_STRUCTURE)
		
		xml_instructions = []
		instr_objects = []	
		for instruction in root:
			#check the tag 
			if (not (instruction.tag =="instruction")):
				sys.exit(InterpretConstants.XML_STRUCTURE)

			#check atrributes
			if(not ("order" in instruction.attrib) or not("opcode" in instruction.attrib) or not (len(instruction.attrib) == 2)):
				sys.exit(InterpretConstants.XML_STRUCTURE)
	
			if(len(instruction) == 0):

				xml_instructions.append(dict(instruction=instruction.attrib["opcode"], order = int(instruction.attrib["order"]), arg_count = 0))
				xml_instruction = dict(instruction=instruction.attrib["opcode"], order = int(instruction.attrib["order"]), arg_count = len(instruction), args = None)
			else:
				argumenty=[]
				for x in instruction: 
					argumenty.append(x)					

				xml_instructions.append(dict(instruction=instruction.attrib["opcode"], order = int(instruction.attrib["order"]), arg_count = len(instruction), args = argumenty))
				xml_instruction = dict(instruction=instruction.attrib["opcode"], order = int(instruction.attrib["order"]), arg_count = len(instruction), args = argumenty)

			self.check_args(xml_instruction)
			instr_objects.append(self.create_object_from_instruction(xml_instruction))
		
		#setrid objekty podle jejich poradi daneho atributem order v xml souboru
		instr_objects.sort(key=lambda x: x.order)
		for x in range(len(instr_objects)):
			if(x < len(instr_objects)-1):
				if(instr_objects[x].order == instr_objects[x+1].order):				
					sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Chyba v XML - vice instrukci se stejnym poradim (atributem order)!\n")
					sys.exit(InterpretConstants.XML_STRUCTURE)

			if(instr_objects[x].__class__.__name__ == 'LABEL'):
				global label_dict
				if(not(instr_objects[x].args[0].value in label_dict.keys())):
					label_dict[instr_objects[x].args[0].value] = x
				else:
					sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Navesti uz existuje!\n")
					sys.exit(InterpretConstants.SEMANTIC_ERROR)

		self.instructions = instr_objects


	def create_object_from_instruction(self, instr):
		s=Switch()
		obj = s.switch(instr)
		if(str(obj) == 'Invalid'):	
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Neexistujici instrukce!!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)	
		return obj

	def getInstructions(self):
		return self.instructions

	#testuje jestli jsou spravne argumenty (napriklad spravne zapsane)
	def check_args(self, instruction):
		if(instruction['arg_count'] > 0):
			for i in range(0,len(instruction['args'])):
				if(instruction['args'][i].tag != "arg"+str(i+1)):
					sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Chyba v XML - spatne zapsany tag argumentu\n")
					sys.exit(InterpretConstants.XML_STRUCTURE)



class Switch:
	def switch(self,ins):
		self.instruction = ins
		self.order = int(self.instruction['order'])
		method_name=str(self.instruction['instruction'].lower())
		#Klicova slova jazyka python nemuzeme pouzit jako nazev funkce a proto ke jmenu funkce pridavame podtrzitko
		if(method_name == 'return'):
			method_name = 'return_'
		elif(method_name == 'and'):
			method_name = 'and_'
		elif(method_name == 'or'):
			method_name = 'or_'
		elif(method_name == 'not'):
			method_name = 'not_'
		elif(method_name == 'break'):
			method_name = 'break_'

		method=getattr(self,method_name,lambda :'Invalid')
		return method()
	def move(self):
		return MOVE(self.instruction, self.order)
	def createframe(self):
		return CREATEFRAME(self.instruction, self.order)
	def pushframe(self):
		return PUSHFRAME(self.instruction, self.order)	
	def popframe(self):
		return POPFRAME(self.instruction, self.order)	
	def defvar(self):
		return DEFVAR(self.instruction, self.order)	
	def call(self):
		return CALL(self.instruction, self.order)	
	def return_(self):
		return RETURN(self.instruction, self.order)	
	def pushs(self):
		return PUSHS(self.instruction, self.order)	
	def pops(self):
		return POPS(self.instruction, self.order)	
	def add(self):
		return ADD(self.instruction, self.order)	
	def sub(self):
		return SUB(self.instruction, self.order)	
	def mul(self):
		return MUL(self.instruction, self.order)	
	def idiv(self):
		return IDIV(self.instruction, self.order)	
	def lt(self):
		return LT(self.instruction, self.order)	
	def gt(self):
		return GT(self.instruction, self.order)	
	def eq(self):
		return EQ(self.instruction, self.order)	
	def and_(self):
		return AND(self.instruction, self.order)	
	def or_(self):
		return OR(self.instruction, self.order)	
	def not_(self):
		return NOT(self.instruction, self.order)	
	def int2char(self):
		return INT2CHAR(self.instruction, self.order)	
	def stri2int(self):
		return STRI2INT(self.instruction, self.order)	
	def read(self):
		return READ(self.instruction, self.order)	
	def write(self):
		return WRITE(self.instruction, self.order)	
	def concat(self):
		return CONCAT(self.instruction, self.order)	
	def strlen(self):
		return STRLEN(self.instruction, self.order)	
	def getchar(self):
		return GETCHAR(self.instruction, self.order)	
	def setchar(self):
		return SETCHAR(self.instruction, self.order)	
	def type(self):
		return TYPE(self.instruction, self.order)	
	def label(self):
		return LABEL(self.instruction, self.order)	
	def jump(self):
		return JUMP(self.instruction, self.order)	
	def jumpifeq(self):
		return JUMPIFEQ(self.instruction, self.order)	
	def jumpifneq(self):
		return JUMPIFNEQ(self.instruction, self.order)	
	def exit(self):
		return EXIT(self.instruction, self.order)	
	def dprint(self):
		return DPRINT(self.instruction, self.order)	
	def break_(self):
		return BREAK(self.instruction, self.order)	


#classes of instructions

class MOVE:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 2)
		self.args = check_arguments(instruction, ['var', 'symb'])

		
	def EXEC(self, ramce):
		ramec_promenne = get_ramec_promenne(ramce, self.args[0])

		if(self.args[1].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[0].value.split("@")[1]
			nazev_promenne_2 = self.args[1].value.split("@")[1]
			if(ramec_promenne_2[nazev_promenne_2] == None):
				sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Snaha ulozit do promenne nedefinovanou hodnotu!\n")
				sys.exit(InterpretConstants.MISSING_VALUE_ERROR)
			ramec_promenne[nazev] = ramec_promenne_2[nazev_promenne_2]

		else:
			nazev = self.args[0].value.split("@")[1]
			ramec_promenne[nazev] = self.args[1].value

class CREATEFRAME:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 0)
		self.args = None

	def EXEC(self, ramce):
		ramce['TF'] = {}


class PUSHFRAME:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 0)
		self.args = None

	def EXEC(self, ramce):
		if(ramce['TF'] == None):
			sys.stderr.write("CHYBA("+str(InterpretConstants.FRAME_ERROR)+"): Snaha ulozit docasny ramec (TF) do lokalniho ramce (LF), pricemz docasny ramec je prazdny!\n")
			sys.exit(InterpretConstants.FRAME_ERROR)
		else:
			ramce['LF_ramce'].append(ramce['TF'])
			ramce['TF'] = None	


class POPFRAME:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 0)
		self.args = None

	def EXEC(self, ramce):
		if(len(ramce['LF_ramce']) == 0):
			sys.stderr.write("CHYBA("+str(InterpretConstants.FRAME_ERROR)+"): Snaha ulozit lokalni ramec (LF) do docasneho ramce (TF), pricemz lokalni ramec je prazdny!\n")
			sys.exit(InterpretConstants.FRAME_ERROR)
		ramce['TF'] = ramce['LF_ramce'][-1]
		ramce['LF_ramce'] = ramce['LF_ramce'][0:-1]


class DEFVAR:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['var'])

		
	def EXEC(self, ramce):
		variable = self.args[0].value.split("@")
		ramec = variable[0]
		nazev = variable[1]

		if(ramec == 'TF'):
			ramce['TF'][nazev] = None
		elif(ramec == 'GF'):
			ramce['GF'][nazev] = None
		elif(ramec == 'LF'):
			ramce['LF_ramce'][-1][nazev] = None

class CALL:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['label'])

		
	def EXEC(self, ramce):
		if(self.args[0].value in label_dict.keys()):
			global IP
			global IP_stack

			IP_stack.append(IP)
			IP = label_dict[self.args[0].value]

		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Nedefinovane navesti!\n")
			sys.exit(InterpretConstants.SEMANTIC_ERROR)

class RETURN:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 0)
		self.args = None

		
	def EXEC(self, ramce):
		global IP
		global IP_stack
		if(len(IP_stack)==0):
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Return bez predchoziho volani CALL!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		IP = IP_stack.pop()

class PUSHS:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['symb'])

		
	def EXEC(self, ramce):
		if(self.args[0].type == 'var'):
			ramec_promenne = get_ramec_promenne(ramce, self.args[0])
			nazev = self.args[0].value.split("@")[1]
			date_stack.append(ramec_promenne[nazev])
		else:
			date_stack.append(self.args[0].value)

class POPS:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['var'])

		
	def EXEC(self, ramce):
		if(len(date_stack) == 0):
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): POPS bez predchoziho volani PUSHS!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		value = date_stack.pop()
		ramec_promenne = get_ramec_promenne(ramce, self.args[0])
		
		nazev = self.args[0].value.split("@")[1]
		ramec_promenne[nazev] = value


class ADD:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri scitani!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
			nazev = self.args[0].value.split("@")[1]
			ramec_vysledne_promenne[nazev] = str(int(arg_1) + int(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne chyby operandu pri scitani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)


class SUB:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri odecitani!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
			nazev = self.args[0].value.split("@")[1]
			ramec_vysledne_promenne[nazev] = str(int(arg_1) - int(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne chyby operandu pri odecitani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

class MUL:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value


		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri nasobeni!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
			nazev = self.args[0].value.split("@")[1]
			ramec_vysledne_promenne[nazev] = str(int(arg_1) * int(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne chyby operandu pri nasobeni!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

class IDIV:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value


		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri deleni!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(arg_2 == '0'):
			sys.stderr.write("CHYBA("+str(InterpretConstants.WRONG_VALUE_ERROR)+"): Deleni nulou!\n")
			sys.exit(InterpretConstants.WRONG_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
			nazev = self.args[0].value.split("@")[1]
			ramec_vysledne_promenne[nazev] = str(int(arg_1) // int(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri deleni!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)


class LT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].value == 'nil'):
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri porovnavani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)
		
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value

		if(arg_1 == None or arg_2 == None):		
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri porovnavani!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			mensi = (int(arg_1) < int(arg_2))
		elif(isBool(arg_1) and isBool(arg_2)):
			if ((str(arg_1).lower()  == "true") and (str(arg_2).lower()  == "false")):
				mensi = True
			else:
				mensi = False	
		elif(isStr(arg_1) and isStr(arg_2)):
			mensi = (str(arg_1) < str(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri porovnavani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = mensi

class GT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].value == 'nil'):
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri porovnavani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)
		
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri porovnavani!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			vetsi = (int(arg_1) > int(arg_2))
		elif(isBool(arg_1) and isBool(arg_2)):
			if ((str(arg_1).lower() == "true") and (str(arg_2).lower()  == "false")):
				vetsi = True
			else:
				vetsi = False
		elif(isStr(arg_1) and isStr(arg_2)):
			vetsi = (str(arg_1) > str(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri porovnavani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = vetsi

class EQ:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[1].value

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			arg_2 = self.args[2].value

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri porovnavani!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1) and isInt(arg_2)):
			rovna_se = (int(arg_1) == int(arg_2))
		elif(isBool(arg_1) and isBool(arg_2)):
			if (str(arg_1).lower() == str(arg_2).lower()):
				rovna_se = True
			else:
				rovna_se= False	
		elif(isNil(arg_1) and isNil(arg_2)):
			pravda="true"
		elif(isStr(arg_1) and isStr(arg_2)):
			rovna_se = (str(arg_1) == str(arg_2))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri porovnavani!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = rovna_se


class AND:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "bool"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri and!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			if(self.args[2].type == "bool"):
				arg_2 = self.args[2].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri and!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri and!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isBool(arg_1) and isBool(arg_2)):
			if (str(arg_1).lower() == "true" ) and (str(arg_1).lower() == str(arg_2).lower()):
				and_ = True
			else:
				and_ = False
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri and!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = and_

class OR:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "bool"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri or!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			if(self.args[2].type == "bool"):
				arg_2 = self.args[2].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri or!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri or!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isBool(arg_1) and isBool(arg_2)):
			if ((str(arg_1).lower() == "true") or (str(arg_2).lower() == "true")):
				or_ = True
			else:
				or_ = False
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri or!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = or_

class NOT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 2)
		self.args = check_arguments(instruction, ['var', 'symb'])

		
	def EXEC(self, ramce):
		
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "bool"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri not!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby hodnota pri not!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isBool(arg_1)):
			if(str(arg_1).lower() == "false"):
				not_ = True
			else:
				not_ = False
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri not!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = not_


class INT2CHAR:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 2)
		self.args = check_arguments(instruction, ['var', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "int" or self.args[1].type == "string"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri prevodu cisla na znak!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby hodnota pri prevodu cisla na znak!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isInt(arg_1)):			
			try:
				char_ = chr(int(arg_1))
			except:
				sys.stderr.write("CHYBA("+str(InterpretConstants.STRING_ERROR)+"): Operand neni validni ordinalni hodnota znaku v Unicode!\n")
				sys.exit(InterpretConstants.STRING_ERROR)
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri prevodu cisla na znak!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = char_

class STRI2INT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "int"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri STRI2INT!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			if(self.args[2].type == "int"):
				arg_2 = self.args[2].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri STRI2INT!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri STRI2INT!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if (isStr(arg_1) and isInt(arg_2)):
			try:
				int_ = ord(str(arg_1)[int(arg_2)])
			except:
				sys.stderr.write("CHYBA("+str(InterpretConstants.STRING_ERROR)+"): Index mimo zadany retezec!\n")
				sys.exit(InterpretConstants.STRING_ERROR)
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri STRI2INT!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = int_

class READ:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 2)
		self.args = check_arguments(instruction, ['var', 'type'])

		
	def EXEC(self, ramce):
		global input_f
		if(input_f == None):
			try:
				data = input()
			except:
				sys.stderr.write("CHYBA("+str(InterpretConstants.FILE_OPEN_ERROR)+"): Chyba pri praci se soubory!\n")
				sys.exit(InterpretConstants.FILE_OPEN_ERROR)
		else:			
			try:
				data = input_f.readLine()
			except:
				sys.stderr.write("CHYBA("+str(InterpretConstants.FILE_OPEN_ERROR)+"): Chyba pri praci se soubory!\n")
				sys.exit(InterpretConstants.FILE_OPEN_ERROR)
		
		if(self.args[1].value == 'int' and isInt(data)):
			input_ = int(data)
		elif(self.args[1].value == 'bool' and isBool(data)):
			input_ = (data.lower() == 'true')
		elif(self.args[1].value == 'string' and isStr(data)):
			input_ = str(data)
		else:
			#inplicitni hodnoty
			if(self.args[1].value == 'int'):
				input_ = 0
			elif(self.args[1].value == 'bool'):
				input_ = False
			elif(self.args[1].value == 'string'):
				input_ = ""
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri READ!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)


		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = input_

class WRITE:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['symb'])

		
	def EXEC(self, ramce):
		if(self.args[0].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[0])

			nazev = self.args[0].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
			isVar = True
		else:
			arg_1 = self.args[0].value
			isVar = False

		if(arg_1 == None):
			print("", end = "")
		elif(isInt(arg_1)):
			print(str(int(arg_1)), end = "")
		elif(isBool(arg_1)):
			print(str(arg_1), end = "")
		elif(isNil(arg_1)):
			print("", end = "")
		elif(isStr(arg_1)):
			print(str(arg_1), end = "")


class CONCAT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "string"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri konkatenaci!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			if(self.args[2].type == "string"):
				arg_2 = self.args[2].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri konkatenaci!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri konkatenaci!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isStr(arg_1) and isStr(arg_2)):
			concat = str(arg_1) + str(arg_2)
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri konkatenaci!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = concat


class STRLEN:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 2)
		self.args = check_arguments(instruction, ['var', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])
			nazev = self.args[1].value.split("@")[1]

			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "string"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri strlen!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)


		if(arg_1 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby hondota v promene pri strlen!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isStr(arg_1)):
			strlenn = len(str(arg_1))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri strlen!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = strlenn

class GETCHAR:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			if(self.args[1].type == "int"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri STRI2INT!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			if(self.args[2].type == "int"):
				arg_2 = self.args[2].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri STRI2INT!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri STRI2INT!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if (isStr(arg_1) and isInt(arg_2)):
			try:
				char_ = str(arg_1)[int(arg_2)]
			except:
				sys.stderr.write("CHYBA("+str(InterpretConstants.STRING_ERROR)+"): Index mimo zadany retezec!\n")
				sys.exit(InterpretConstants.STRING_ERROR)
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri STRI2INT!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = char_

class SETCHAR:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['var', 'symb', 'symb'])

		
	def EXEC(self, ramce):		

		ramec_vystupni_promene = get_ramec_promenne(ramce, self.args[0])

		nazev = self.args[0].value.split("@")[1]
		vystupni_promena = ramec_vystupni_promene[nazev]

		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:			
			if(self.args[1].type == "int"):
				arg_1 = self.args[1].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri SETCHAR!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
		else:
			if(self.args[2].type == "string"):
				arg_2 = self.args[2].value
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri SETCHAR!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri SETCHAR!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isStr(vystupni_promena) and isInt(arg_1) and isStr(arg_2)):
			try:
				mezikrok=list(str(vystupni_promena))
				mezikrok[int(arg_1)] = arg_2[0]
				nazev = self.args[0].value.split("@")[1]
				vystupni_promena=''.join(mezikrok)
				ramec_vystupni_promene[nazev] = vystupni_promena

			except:
				print("aaa",arg_2[0])
				sys.stderr.write("CHYBA("+str(InterpretConstants.STRING_ERROR)+"): Indexace mimo retezec nebo prazdny retezec!\n")
				sys.exit(InterpretConstants.STRING_ERROR)
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne chyby operandu pri SETCHAR!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)

class TYPE:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 2)
		self.args = check_arguments(instruction, ['var', 'symb'])

		
	def EXEC(self, ramce):
		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]

			isVar = True
		else:	
			t = self.args[1].type		
			if(t == "int" or t == "bool" or t == "string" or t == "nil"):
				arg_1 = self.args[1].value
				isVar = False
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatny typ operandu pri TYPE!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

		if(arg_1 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby hodnota pri TYPE!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isVar):
			if(isInt(arg_1)):
				type_ = "int"
			elif(isBool(arg_1)):
				type_ = "bool"
			elif(isNil(arg_1)):
				type_ = "nil"
			elif(isStr(arg_1)):
				type_ = "string"
		else:
			type_ = self.args[1].type

		ramec_vysledne_promenne = get_ramec_promenne(ramce, self.args[0])
		nazev = self.args[0].value.split("@")[1]
		ramec_vysledne_promenne[nazev] = type_	

class LABEL:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['label'])

		
	def EXEC(self, ramce):
		return

class JUMP:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['label'])

		
	def EXEC(self, ramce):
		global IP
		global IP_stack
		global label_dict

		if(self.args[0].value in label_dict.keys()):

			IP = label_dict[self.args[0].value]

		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Nedefinovane navesti!\n")
			sys.exit(InterpretConstants.SEMANTIC_ERROR)


class JUMPIFEQ:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['label', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		global IP
		global IP_stack

		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
			isVar1 = True
		else:
			arg_1 = self.args[1].value
			isVar1 = False

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
			isVar2 = True
		else:
			arg_2 = self.args[2].value
			isVar2 = False

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri JUMPIFEQ!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isVar1 or isVar2):
			if(isInt(arg_1) and isInt(arg_2)):
				eq = (int(arg_1) == int(arg_2))
			elif(isBool(arg_1) and isBool(arg_2)):
				eq = (arg_1 == arg_2)
			elif(isNil(arg_1) and isNil(arg_2)):
				eq = (arg_1 == arg_2)
			elif(isStr(arg_1) and isStr(arg_2)):
				eq = (str(arg_1) == str(arg_2))
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri JUMPIFEQ!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

			if(eq):
				if(self.args[0].value in label_dict.keys()):

					IP = label_dict[self.args[0].value]

				else:
					sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Nedefinovane navesti!\n")
					sys.exit(InterpretConstants.SEMANTIC_ERROR)
		else:
			if(self.args[1].type == self.args[2].type):
				if(self.args[1].value == self.args[2].value):					
					if(self.args[0].value in label_dict.keys()):

						IP = label_dict[self.args[0].value]

					else:
						sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Nedefinovane navesti!\n")
						sys.exit(InterpretConstants.SEMANTIC_ERROR)
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri JUMPIFEQ!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

class JUMPIFNEQ:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 3)
		self.args = check_arguments(instruction, ['label', 'symb', 'symb'])

		
	def EXEC(self, ramce):
		global IP
		global IP_stack

		if(self.args[1].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[1])

			nazev = self.args[1].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
			isVar1 = True
		else:
			arg_1 = self.args[1].value
			isVar1 = False

		if(self.args[2].type == 'var'):
			ramec_promenne_2 = get_ramec_promenne(ramce, self.args[2])

			nazev = self.args[2].value.split("@")[1]
			arg_2 = ramec_promenne_2[nazev]
			isVar2 = True
		else:
			arg_2 = self.args[2].value
			isVar2 = False

		if(arg_1 == None or arg_2 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri JUMPIFEQ!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)

		if(isVar1 or isVar2):
			if(isInt(arg_1) and isInt(arg_2)):
				eq = (int(arg_1) == int(arg_2))
			elif(isBool(arg_1) and isBool(arg_2)):
				eq = (arg_1 == arg_2)
			elif(isNil(arg_1) and isNil(arg_2)):
				eq = (arg_1 == arg_2)
			elif(isStr(arg_1) and isStr(arg_2)):
				eq = (str(arg_1) == str(arg_2))
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri JUMPIFEQ!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

			if(not eq):
				if(self.args[0].value in label_dict.keys()):
					IP = label_dict[self.args[0].value]

				else:
					sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Nedefinovane navesti!\n")
					sys.exit(InterpretConstants.SEMANTIC_ERROR)
		else:
			if(self.args[1].type == self.args[2].type):
				if(self.args[1].value != self.args[2].value):					
					if(self.args[0].value in label_dict.keys()):
						IP = label_dict[self.args[0].value]

					else:
						sys.stderr.write("CHYBA("+str(InterpretConstants.SEMANTIC_ERROR)+"): Nedefinovane navesti!\n")
						sys.exit(InterpretConstants.SEMANTIC_ERROR)
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri JUMPIFEQ!\n")
				sys.exit(InterpretConstants.OPERAND_ERROR)

class EXIT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['symb'])

		
	def EXEC(self, ramce):
		if(self.args[0].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[0])

			nazev = self.args[0].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
			isVar = True
		else:
			arg_1 = self.args[0].value
			isVar = False

		if(arg_1 == None):			
			sys.stderr.write("CHYBA("+str(InterpretConstants.MISSING_VALUE_ERROR)+"): Chyby jedna z hodnot pri EXIT!\n")
			sys.exit(InterpretConstants.MISSING_VALUE_ERROR)
		
		if(isVar):
			if(isInt(arg_1)):
				ok = True
		else:
			if(self.args[0].type == 'int'):
				ok = True

		if(ok):
			if (int(arg_1) >= 0  and int(arg_1) <= 49):
				sys.exit(int(arg_1))
			else:
				sys.stderr.write("CHYBA("+str(InterpretConstants.WRONG_VALUE_ERROR)+"): Nevalidni hodnota!\n")
				sys.exit(InterpretConstants.WRONG_VALUE_ERROR)
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.OPERAND_ERROR)+"): Spatne typy operandu pri EXIT!\n")
			sys.exit(InterpretConstants.OPERAND_ERROR)
class DPRINT:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 1)
		self.args = check_arguments(instruction, ['symb'])

		
	def EXEC(self, ramce):
		if(self.args[0].type == 'var'):
			ramec_promenne_1 = get_ramec_promenne(ramce, self.args[0])

			nazev = self.args[0].value.split("@")[1]
			arg_1 = ramec_promenne_1[nazev]
		else:
			arg_1 = self.args[0].value
		
		sys.stderr.write(arg_1)
		
class BREAK:
	def __init__(self,instruction, order):
		self.order=order 
		right_num_of_args(instruction, 0)
		self.args = None

		
	def EXEC(self, ramce):		
		global IP
		global IP_stack
		global label_dict

		msg = "Pocet zpracovanych instrukci: " + str(IP+1) +"\nNavesti v programu (a odkazy kde zacinaji): " + str(label_dict) + "\nRamce: " + str(ramce) + "\n"
		sys.stderr.write(msg)









class Argument:
	def __init__(self,xml_arg, value = None, general_type = None, type_ = None):
		self.xml_arg = xml_arg 
		self.value = value 
		self.general_type = general_type
		self.type = type_
		self.check_value()

	def check_value(self):
		if(self.type == 'var' and not re.search("^(TF|LF|GF)@((\w|-|[_$&%*!?])(\w|-|[_$&%*!?]|[0-9])*)$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu var, ale neobsahuje promennou!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(self.type == 'int' and not re.search("^(\+|\-)?\d+$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu int, ale neobsahuje cislo ve spravnem formatu!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(self.type == 'bool' and not re.search("^([tT]{1}[rR]{1}[uU]{1}[eE]{1}|[fF]{1}[aA]{1}[lL]{1}[sS]{1}[eE]{1})$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu bool, ale neobsahuje true nebo false!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(self.type == 'string' and not re.search("^(\w|[^(\s\\\)]|\d|\\\([0-9][0-9][0-9]))*$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu string, ale neobsahuje retezec ve spravnem formatu!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(self.type == 'label' and not re.search("^((\w|-|[_$&%*!?])(\w|-|[_$&%*!?]|[0-9])*)$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu label, ale neobsahuje navesti ve spravnem formatu!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(self.type == 'type' and not re.search("^(int|string|bool)$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu type, ale neobsahuje int|string|bool!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

		if(self.type == 'nil' and not re.search("^nil$",str(self.value))):
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Argument je typu nil, ale neobsahuje nil!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

class Interpret:
	def __init__(self, objekty_instr):
		self.objekty_instrukce = objekty_instr
		self.ramce = self.ramce()

	def EXEC(self):
		global IP
		while IP < len(self.objekty_instrukce):
			self.objekty_instrukce[IP].EXEC(self.ramce)
			IP = IP + 1
		#print(str(self.ramce))
	def ramce(self):
		gf = {}#{'asd':5}
		lf_ramce = []#[{'aed':45},8,6]
		tf = None
		return dict(GF = gf, LF_ramce = lf_ramce, TF = tf)

#verejne funkce
def check_arguments(instruction, types):#var symb
	args = instruction['args']
	Argumenty = []
	idx=-1
	for a in args:
		idx=idx+1
		if(a.attrib["type"] in ["var","int","bool","string","nil"] and types[idx] == 'symb'):
			Argumenty.append(Argument(a,a.text, 'symb', a.attrib["type"]))
		elif(a.attrib["type"] in ["var"] and types[idx] == 'var'):
			Argumenty.append(Argument(a,a.text, 'var', a.attrib["type"]))
		elif(a.attrib["type"] in ["label"] and types[idx] == 'label'):
			Argumenty.append(Argument(a,a.text, 'label', a.attrib["type"]))
		elif(a.attrib["type"] in ["type"] and types[idx] == 'type'):
			Argumenty.append(Argument(a,a.text, 'type', a.attrib["type"]))
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Spatne argumenty!\n")
			sys.exit(InterpretConstants.XML_STRUCTURE)

	return Argumenty

def right_num_of_args(instruction, num_of_args):
	if(instruction['arg_count']!=num_of_args):			
		sys.stderr.write("CHYBA("+str(InterpretConstants.XML_STRUCTURE)+"): Spatny pocet argumentu!\n")
		sys.exit(InterpretConstants.XML_STRUCTURE)

#Funcke vrati promennou (z ramce ve kterem se nachazi)
def get_ramec_promenne(ramce, var):
	variable = var.value.split("@")
	ramec = variable[0]
	nazev = variable[1]

	if(ramec == "GF"):
		ramec_promenne = ramce['GF']
	elif(ramec == "LF"):
		if(len(ramce['LF_ramce']) > 0):
			ramec_promenne = ramce['LF_ramce'][-1]
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.FRAME_ERROR)+"): Snaha cist z lokalniho ramce(LF), ktery je prazdny!\n")
			sys.exit(InterpretConstants.FRAME_ERROR)
	elif(ramec == "TF"):
		if(ramce['TF'] != None):
			ramec_promenne = ramce['TF']
		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.FRAME_ERROR)+"): Snaha cist z nevytvoreneho docasneho ramce (TF)!\n")
			sys.exit(InterpretConstants.FRAME_ERROR)

	if(nazev in ramec_promenne.keys()):
		return ramec_promenne
	else:
		sys.stderr.write("CHYBA("+str(InterpretConstants.VARIABLE_ERROR)+"): Promenna neni ulozena v ramci!\n")
		sys.exit(InterpretConstants.VARIABLE_ERROR)


def isInt(value):
	if(re.search("^(\+|\-)?\d+$",str(value))):
		return True
	else:
		return False

def isBool(value):
	if(re.search("^([tT]{1}[rR]{1}[uU]{1}[eE]{1}|[fF]{1}[aA]{1}[lL]{1}[sS]{1}[eE]{1})$",str(value))):
		return True
	else:
		return False

def isNil(value):
	if(re.search("^nil$",str(value))):
		return True
	else:
		return False

def isStr(value):
	if(re.search("^(\w|[^(\s\\\)]|\d|\\\([0-9][0-9][0-9]))*$",str(value))):
		return True
	else:
		return False












#Trida Program, ktera kontroluje argumenty a ridi cinnost interpretu
class Program:
	def checkArgs(self):
		source_file = ""
		input_file = ""
		if(len(sys.argv) > 1 and len(sys.argv) < 4):
			if((sys.argv[1].upper()) == "--HELP"):
				if(len(sys.argv) == 2):
					print("Napoveda...")
					sys.exit(InterpretConstants.OK)
				else:
					sys.exit(InterpretConstants.ARG_ERROR)

			else:
				source_arg = False
				input_arg = False
				for a in sys.argv:
					if("--source=" in a):
						source_arg = True
						idx = a.index("=")
						source_file = a[idx+1:]
						try:
							self.source = open(source_file, "r")
						except:							
							sys.stderr.write("CHYBA("+str(InterpretConstants.FILE_OPEN_ERROR)+"): Chyba pri otvirani souboru s xml reprezentaci kodu (source)!\n")
							sys.exit(InterpretConstants.FILE_OPEN_ERROR)
					if("--input=" in a):
						input_arg = True
						idx = a.index("=")
						input_file = a[idx+1:]
						try:
							self.input = open(input_file, "r")
						except:							
							sys.stderr.write("CHYBA("+str(InterpretConstants.FILE_OPEN_ERROR)+"): Chyba pri otvirani souboru se vstupy (input)!\n")
							sys.exit(InterpretConstants.FILE_OPEN_ERROR)

				if(source_arg or input_arg):
					if(len(sys.argv) == 3 ):
						if((not source_arg) or (not input_arg)):
							sys.stderr.write("CHYBA("+str(InterpretConstants.ARG_ERROR)+"): Spatne zadane argumenty!\n")
							sys.exit(InterpretConstants.ARG_ERROR)
					if(len(sys.argv) == 2 ):
						if(not source_arg):
							self.source = None
						if(not input_arg):
							self.input = None


				else:
					sys.stderr.write("CHYBA("+str(InterpretConstants.ARG_ERROR)+"): Spatne zadane argumenty!\n")
					sys.exit(InterpretConstants.ARG_ERROR)

		else:
			sys.stderr.write("CHYBA("+str(InterpretConstants.ARG_ERROR)+"): Spatne zadane argumenty!\n")		
			sys.exit(InterpretConstants.ARG_ERROR)


	def main(self):
		#check args and load input and source file
		self.checkArgs()

		global input_f		

		if(self.input == None):
			global input_is_set
			input_is_set = False
		else:
			input_f = self.input
			
		if(self.source == None):
			global source_is_set
			source_is_set = False

		xmlChecker = XMLChecker()
		ins = xmlChecker.check(self.source)

		instructions = xmlChecker.getInstructions()

		interpret = Interpret(instructions)
		interpret.EXEC()

		if(self.input != None):
			input_f.close()

		#print("KONEC")

program = Program()
program.main()


#TODO:
#Pri porovnavani aby pri porovnani napr string a bool vyhodilu chybu