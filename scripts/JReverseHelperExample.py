from com.jreverse.jreverse.Core import JReverseScriptingCore
#import the class you want to use here

objs = JReverseScriptingCore.GetClassInstances("class you want to use") #Format: org/example/example ,There are no dots, Replace them with forward slashes

for obj in objs:
    #Call Functions
    #Modify Fields
    #Modify any part of the class.

#Validate there were no errors.
print("Program Success")