# how to measure runtime

to measure runtime and causes you can add measurement hooks in your code and
will get an report if the execution of the top most block is done.

## Building Blocks

Each try-block will measure its runtime.

```java
${sampleWithReport}
```

... and generate an report like:

```
${sampleWithReport.report}
```

