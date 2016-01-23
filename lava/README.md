# Lava

A strongly typed language inspired in Python, running on a JVM.

## Showcase

```
class ClassName:
	int a = 3

	int function(string b):
		return 10

	string generic(bool t):
		while(true):
			print(“Hello!”)
		if(t):
			oi()
		else:
			tchau()
```

### Create the executable JAR

```
$ mvn clean compile assembly:single
```

The executable jar will be created on target/lava-*.

Execute with `java -jar lava.jar`
