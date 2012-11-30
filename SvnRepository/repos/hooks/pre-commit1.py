#!/bin/env python

#Notes:
#In order to see in eclipse the last version also update from eclipse is required.
#in order to see the revisioned properties use:
#svn proplist D:\aop\downloads\workspace\test2906\src\observer\o.txt@78 -v
#while 78 is example for the revision

#what if there are a lot of commits in single action?

#Testing: change the event_file, ci, and verify the property changed in the observer_file.
#(recommended, not a must:) update the observer_file 
import sys, os, string, re, subprocess

#with removable memory card (I)
BASE_PATH = "I:\\aopWorkspace\\eclipseWorkspace\\thesis.delivery.testhooks.observer\\"
DATA_FILE = BASE_PATH + "src\\data\\d.txt"            
REPOSITORY= "I:\\aopWorkspace\\repos" #I:\\aopWorkspace\\repos"
OBSERVER_FILE= BASE_PATH +"src\\observer\\o.txt"
EVENT_FILE_RELATIVE_PATH = "thesis.delivery.testhooks.observer/src/event/e.txt"

#at work
#DATA_FILE = "D:\\aop\\workspace\\test2906\\src\\data\\d.txt"
#SVN="C:\\Program Files\\CollabNet Subversion Server\\svn.exe"
#SVNLOOK="C:\\Program Files\\CollabNet Subversion Server\\svnlook.exe"
#REPOSITORY="D:\\repos"
#OBSERVER_FILE="D:\\aop\\workspace\\test2906\\src\\observer\\o.txt"
#EVENT_FILE_RELATIVE_PATH = "test2906/src/event/e.txt"

#at home
#DATA_FILE = "D:\\aop\downloads\workspace\\test2906\src\data\d.txt"
SVN="C:\\Program Files\\CollabNet Subversion Server\\svn.exe"
SVNLOOK="C:\\Program Files\\CollabNet Subversion Server\\svnlook.exe"
#SVN="C:\\Program Files\\Subversion\\bin\\svn.exe"
#SVNLOOK="C:\\Program Files\\Subversion\\bin\\svnlook.exe"
#REPOSITORY="D:\\aop\\repos"
#OBSERVER_FILE="D:\\aop\\downloads\\workspace\\test2906\\src\\observer\\o.txt"
#EVENT_FILE_RELATIVE_PATH = "test2906/src/event/e.txt"

def ExecuteCommand(cmd):
    try:
        proc = subprocess.Popen(cmd, stdin=None, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (stdout, stderr) = proc.communicate(None)
        return (proc.returncode, stdout.decode("UTF8"), stderr.decode("UTF8"))
    except Exception as ex:
        return (ex.errno, "A   test2906/sds", ex.strerror)

#update the file properties from the repository to the local working copy 
#currently not in use, but was tested and worked, might be in use in the future
def update():
    print("update")
    cmd = SVN + " update " + OBSERVER_FILE                                                    
    (retCode, stdout, stderr) = ExecuteCommand(cmd)
    f = open(DATA_FILE, 'w')
    f.write("stdout: " + stdout +"\n")
    f.write("stderr: " + stderr +"\n")

#set property
def setProp():
    print("setProp")
    cmd = SVN + " propset myAspects 'aspect13' " + OBSERVER_FILE                                                    
    (retCode, stdout, stderr) = ExecuteCommand(cmd)
    f = open(DATA_FILE, 'w')
    f.write("stdout: " + stdout +"\n")
    f.write("stderr: " + stderr +"\n")                    

#check in (commit) the file with the updated properties
def ci():
    print("ci")    
    cmd = SVN + " ci " + OBSERVER_FILE + ' -m ""'                                                    
    (retCode, stdout, stderr) = ExecuteCommand(cmd)
    f = open(DATA_FILE, 'w')
    f.write("stdout: " + stdout +"\n")
    f.write("stderr: " + stderr +"\n")
        
def main(repos, txn):
#    #1. create new revision of the same class
#    #2. set property "test:sagi"+version name for the version
    f = open(DATA_FILE, 'w')
    myData = "repos= "+ repos +" txn="+txn +"\n"
    f.write(myData)
    curFile = getCurrentFile(txn)
    f.write("current file = "+ curFile)
    if (curFile == EVENT_FILE_RELATIVE_PATH):
        setProp()        
        #update()        
        ci()
        #good:         
        f.write("finish scc\n")
        #if retcode !=0 :
        #    sys.exit(2)

def filename(line):
    if line:
        return line[4:].split()[0]
    else:
        return ""   #return line[4:].split()[0]

def getCurrentFile(txn):        
    f = open(DATA_FILE+"1", 'w')
    f.write("stdout = "+txn)		   
    cmd = SVNLOOK + " changed " + REPOSITORY +" -t " + txn 
    (retCode, stdout, stderr) = ExecuteCommand(cmd)
    print("stdout = "+stdout)
    f.write("cmd = "+cmd +"\n" + "stdout = "+stdout + "stderr = " + stderr)
    f.write("cmd = "+cmd +"\n" + "stdout = "+stdout + "stderr = " + stderr)
    #get only the filename... (A   test2906/)
    return filename(stdout)     

if __name__ == '__main__':
    if len(sys.argv) < 3:
        sys.stderr.write("Usage: %s REPOS TXN\n" % (sys.argv[0]))
    else:        
        main(sys.argv[1], sys.argv[2])
        #sys.stderr.write("sagi: Test std error")
sys.exit(0)
