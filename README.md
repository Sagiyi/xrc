# XRC: Crosscutting Revision Control  #
## The Problem in a Nutshell ##
Viewing a previous version of a class, or comparing two versions of a class "hides" the crosscutting effects of aspects on the versioned classes. 

Visualizing the crosscutting effects is vital for  understanding the full functionality of a class.
AJDT visualize the crosscutting effects via markers for the current build, however, there is no support in AJDT for tracking these markers in previous versions of the software. Consequently, 
It is difficult to discover which aspects advised previous versions of a class. It is especially difficult to compare two versions and review any changes to the crosscutting effect.

## The Solution in a Nutshell ##
The XRC approach improves the revision control (RC) support for aspect oriented programming (AOP). It features enrichment, persistence, comparison and display of crosscutting effects for this purpose.

The XRC plug-in implements the XRC approach as an Eclipse plug-in that extends the JDT, AJDT, and SVN plug-ins. The XRC plug-in provides crosscutting revision control (XRC) for developers that use the Subversion (SVN) revision control system and the aspectJ language.

The XRC architecture is flexible, and easy to adapt for other revision crontrol systems, and other meta-data.

The content of the folders is as following:

- **Docs** - User Guide and Developer Guide (currently can be found in the thesis appendixes).
- **EmbeddedInstallation** - Contains the required files for XRC plug-in installation within an already installed Eclipse IDE.
- **SvnRepository** - The SVN Repository contains the XRC Source Code, a Test Suite and the Bank Application examples updated at the release time.
- **Source** - Contain the XRC Source Code, a Test Suite and the Bank Application examples updated at the release time.

Note: The **XRC Standalone Installation** contains Eclipse with the XRC plug-in, as well as AJDT and SVN plug-ins. It is not commited to the github, however it is available on my sites:

[http://www.cslab.openu.ac.il/research/xrc/index.html](http://www.cslab.openu.ac.il/research/xrc/index.html "my XRC site in openu")

[https://sites.google.com/site/sagiifrahresearch/publications](https://sites.google.com/site/sagiifrahresearch/publications "my google site")

## Who Needs It?##
Suppose you develop code using aspects or your code is affected by aspects (else go away :)). You may want to:

- Imporve change control. When modifying an aspect, before check-in, you may want to review all the classes that their  behaviour was modified, and/or mark them automatically in a traceble manner.
- Discover conflicts easier. When a class functionality is modified by different developers via aspects (and/or on the class itself), you may want to be aware and decide how to handle the conflict before check-in. Without XRC this kind of conflict will be oversight.   
- Ease bug investigation. 
	- you may want to open a previous version of a class or an aspect, and understand its functionality (including functionality that was modified by aspects).
	- You may want to be able to find which version of an aspect advised your class, or vise versa, on which versioned classes did the aspect affect.
	- You may want to find the differences in the aspectual effects when comparing two versions of a classes or an aspect.
- See the whole picture.
	- You may want to track and understand the evolution of a feature includying the crosscutting effects that may be involved.
	- You may want to research the behaviour of the system in a previous version includying the crosscutting effects, without checking it out and build it.




## Quick Start ##
Open the user guide from Docs, (currently in the thesis appendixes), and follow the instructions.