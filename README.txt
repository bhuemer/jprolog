This is a piece of coursework that I had to submit at university as part
of the programming course that I needed to attend in the second term
(Object Oriented Programming:
http://www-typo3.cs.ucl.ac.uk/students/syllabus/undergrad/1008_object_oriented_programming/).

== COMP1008 - Java coursework

== Known limitations:
As you will see, I chose to implement the Prolog interpreter, but I only
implemented basic features. The only terms you can use are constants,
predicates and variables (so, for example, I didn't implement lists).
Furthermore there are no builtin predicates available, like, for example,
there's no less-than predicate ("<") available, no forall predicate, etc.
which means that you can only evaluate simple Prolog queries. They will,
however, work.

== How to use / start the application:
Basically you just have to execute the main class, namely
"uk.ac.ucl.cs.programming.coursework.main.Application". There are no
third party libraries required in order to compile this project, which
is why I thought it's okay if I just submit the source files without any
build files. However, the IntelliJ module file is included, in case you're
using IntelliJ as well.

The interpreter will give you a short introduction on how to use it
anyway, but basically it works like SWI Prolog. Using the square bracket
notation you can load files (note that every time you load a file all
previously loaded rules will be dropped - it's a total refresh every
single time), using the normal query syntax you can execute queries,
and so on.

Just as SWI Prolog, this interpreter presents you with solutions one
after another. So unless you enter ',' you won't see any further
results.

Additionally I have included a set of test cases in the form of JUnit
tests. Any JUnit 4.x JAR file will be sufficient in order to execute
those tests and as most IDEs nowadays provide those library files
anyway I didn't include them in the archive file.

== Resources
There are basically just two parts in this application for which I searched
for information, unification and the backtracking evaluation approach. This
page (http://www.cs.rpi.edu/~freems/proj3/Unification_and_Variables.htm)
explains how the Robinson Unification algorithm works, which is basically
the algorithm that I've implemented, just more in terms of objects rather
than what this description states. The backtracking approach / rule
evaluation mechanism has been implemented according to some COMP1007
Lecture notes. 

