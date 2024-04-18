# VF2 - algoritm pentru detectarea izomorfismului între două grafuri

## Introducere
Problema izomorfismului pe grafuri este unul dintre cei mai importanți și mai studiați subiecte în domeniul teoriei grafurilor și algoritmică. Această problemă implică determinarea dacă două grafuri sunt structural identice, mai formal daca există o bijectie între nodurile grafului 1 și nodurile grafului 2 astfel încât două noduri sunt adiacente în graful 1 dacă și numai dacă nodurile corespunzătoare sunt adiacente în graful 2.

De-a lungul timpului au fost propuse mai multe abordări pentru rezolvarea acestei probleme, una dintre cele mai cunoscute fiind algoritmul VF2. Acesta a fost propus de Luigi P. Cordella, Pasquale Foggia, Carlo Sansone și Mario Vento în 2004, fiind o imbunatatire a primei versiuni VF aparuta in 2001. 

Algoritmul VF2 este un algoritm de backtracking, inspirat din algoritmul lui Ullman din 1976.
El s-a dovedit a fi unul dintre cei mai eficienti algoritmi pentru detectarea izomorfismului între două grafuri, fiind varianta cea mai folosita in majoritatea librariilor de grafuri.

Desigur, algoritmul a mai avut o serie de imbunatatiri, in acest moment fiind disponibila versiunea VF3, dar si versiuni paralele ale acestuia. 
Insa varianta de baza, VF2, este in continuare cea mai folosita si cea mai studiata.

## Descriere algoritm
Spatiul de cautare al algoritmului este format din toate starile partiale si eventual stari finale(complete).

O stare in cadrul algoritmului este definita de:
- doua grafuri G1 si G2
- maparea curenta M, care contine perechi de noduri (n1, n2) din cele doua grafuri, unde n1 apartine lui G1 si n2 apartine lui G2
    - notam: M1 = {noduri mapate apartinand lui G1}, M2 = {noduri mapate apartinand lui G2}
    - notam: G1(s) = subgraful generat de multimea de noduri M1, G2(s) = subgraful generat de multimea de noduri M2
- vectorul de marcare 'in1'/'in2' si 'out1'/'out2':
    - in[i] > 0, daca nodul i din G1 este ori mapat(apartine lui M1), ori este un 'in-going node' in subgraful G1(s) (adica este predecesor al lui i)
    - in[i] = 0, daca nodul i din G1 nu a fost explorat inca(nici un vecin al sau nu a fost mapat)
- contorul de noduri 'len_in1'/'len_in2' , 'len_out1'/'len_out2' si 'len_both1'/'len_both2':
    - len_in1 = |{noduri care sunt 'in-going' in subgraful G1(s)}|, se numara si nodurile mapate
    - len_out1 = |{noduri care sunt 'out-going' in subgraful G1(s)}|, se numara si nodurile mapate
    - len_both1 = |{noduri care sunt 'in-going' si 'out-going' in subgraful G1(s)}|, se numara si nodurile mapate

Abstractizare:
- notam cu T1_in(s) = {noduri care sunt 'in-going' in subgraful G1(s)}
- notam cu T1_out(s) = {noduri care sunt 'out-going' in subgraful G1(s)}
- T1(s) = T1_in(s) U T1_out(s) 
- similar pentru G2, T2_in(s) si T2_out(s) si T2(s)

Structura algoritmului:
```
    PROCEDURE Match(s)
    INPUT: a partial state s(the initial state s0 has M(s0) = {})
    OUTPUT: the mappings between the two graphs
    
    IF M(s) covers all the nodes
    THEN 
        OUTPUT M(s)
    ELSE
        Compute the set P(s) of the pairs candidate for inclusion in M(s)
        FOREACH p in P(s)
            IF the feasibility rules succeed for the inclusion of p in M(s)
            THEN
                Construct a new state s' by adding p to M(s)
                CALL Match(s')
            END IF
        END FOREACH
    END IF 
```

Generarea setului de perechi candidate pentru mapare:
- daca exista noduri n1 din T1(s) care sa fie si 'in' si 'out' + daca exista noduri n2 din T2(s) care sunt si 'in' si 'out',
atunci se alege ca pereche candidat (n1, n2)
- altfel, daca T1_out(s) != O si T2_out(s) != O, se alege ca pereche candidat (n1, n2), unde n1 este un din T1_out(s) si n2 este un din T2_out(s)
- altfel, daca T1_in(s) != O si T2_in(s) != O, se alege ca pereche candidat (n1, n2), unde n1 este un din T1_in(s) si n2 este un din T2_in(s)
- altfel, se alege n1 din N1 - M1(s) si n2 din N2 - M2(s), unde N1 si N2 sunt multimea de noduri din G1 si G2

Regulile de fezabilitate, pentru izomorfism exact, sunt urmatoarele:
- n1 si n2 sunt perechea candidat, n1 apartine lui G1, n2 apartine lui G2
- R_pred: n' predecesor al lui n1 si n' apartine lui M1(s), deci este mapat la un nod m' din M2(s) => ATUNCI m' trebuie sa fie predecesor al lui n2 
- R_succ: n' succesor al lui n1 si n' apartine lui M1(s), deci este mapat la un nod m' din M2(s) => ATUNCI m' trebuie sa fie succesor al lui n2
- R_in: numarul de succesori/predecesori ai lui n1 care sunt in T1_in(s) este egal cu numarul de succesori/predecesori ai lui n2 care sunt in T2_in(s)
- R_out: numarul de succesori/predecesori ai lui n1 care sunt in T1_out(s) este egal cu numarul de succesori/predecesori ai lui n2 care sunt in T2_out(s)
- R_new: numarul de succesori/predecesori ai lui n1 care nu sunt nici in T1(s) nici in M1(s)(deci noduri 'new') este egal cu numarul de succesori/predecesori ai lui n2 care nu sunt nici in T2(s) nici in M2(s)(deci noduri 'new')

Prin aceste reguli se asigura:
- ca daca se adauga o pereche de noduri in mapare, aceasta mapare este valida si nu va duce la un izomorfism gresit.
- ca vom elimina din spatiul de cautare starile care nu pot duce la o mapare valida

Mai exista si o alta regula de 'pruning', care ajuta la reducerea spatiului de cautare:
- spunem ca o stare s este 'dead'(nu va duce nicioadata la o mapare valida) daca:
  - len_in1(s) != len_in2(s) sau len_out1(s) != len_out2(s) sau len_both1(s) != len_both2(s)


Pentru subgraph isomorphism(pentru G1 gasim un subgraf in G2 izomorfic cu cel dintai), algoritmul este similar:
- pentru fezabilitate, regulile R_in, R_out si R_new sunt modificate astfel:
  - egalitatea devine 'mai mic sau egal'
- pentru 'pruning', regula de 'dead' este modificata astfel:
  - len_in1(s) > len_in2(s) sau len_out1(s) > len_out2(s) sau len_both1(s) > len_both2(s)
