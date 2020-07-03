grammar MyAssign;
assign : ID '=' expr ';' ;
ID : [a-z]+ ;
expr : NUMBER ;
NUMBER : [1-9][0-9]*|[0]|([0-9]+[.][0-9]+) ;