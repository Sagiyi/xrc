package thesis.delivery.testsuite.formalexample;

public aspect A {
	before(): execution(void m11()){ System.out.println("before m11"); }
}