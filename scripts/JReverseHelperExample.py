from com.jreverse.jreverse.Core import JReverseScriptingCore
from org.example import coolclass

objs = JReverseScriptingCore.GetClassInstances("Lorg/example/coolclass;") #Format: Lorg/example/coolclass; ,Uses JNI formating for class names. This allows for more freedom if you want different types

for obj in objs:
    #Call Functions
    #Modify Fields
    #Modify any part of the class.

#Validate there were no errors.
print("Program Success")