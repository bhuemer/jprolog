father_of(paul,tom).
father_of(paul,roy).
father_of(bill,sarah).
father_of(bill,penny).
father_of(bill,june).
father_of(tom,greta).
father_of(tom,david).
father_of(david,jerry).

mother_of(helen,tom).
mother_of(helen,roy).
mother_of(abigail,sarah).
mother_of(abigail,penny).
mother_of(abigail,june).
mother_of(sarah,greta).
mother_of(sarah,david).

age_is(tom,30).
age_is(roy,23).
age_is(sarah,35).
age_is(penny,30).
age_is(june,27).
age_is(greta,17).
age_is(david,26).
age_is(jerry,4).

male(roy).
male(jerry).
male(M) :- father_of(M, _).

female(greta).
female(penny).
female(june).
female(F) :- mother_of(F, _).

person(P) :- male(P).
person(P) :- female(P).

parent_of(P, C) :- father_of(P, C).
parent_of(P, C) :- mother_of(P, C).

grand_father_of(GF, GC) :- father_of(GF,P), parent_of(P,GC).

ancestor_of(A,D) :- parent_of(A,D).
ancestor_of(A,D) :- parent_of(P,D), ancestor_of(A,P).