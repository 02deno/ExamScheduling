<!-- TABLE OF CONTENTS -->
## Table of Contents

- [Table of Contents](#table-of-contents)
- [Distribution of Tasks](#distribution-of-tasks)
- [Defining constant term](#defining-constant-terms)
  - [Naming Convention](#naming-convention)
- [Comment Format](#comment-format)
- [Automatic Documentation Extension](#documentation-extension)
- [Folder Structure ](#folder-structure)
- [Functions to implement](#)
    - [Generating data/input/constraints](#generating-data)
    - [Encoding of chromosomes, genes](#encoding)
    - [Initialization of population ](#initialization-of-population) 
    - [Fitness Calculation](#fitness-calculation) 
    - [Selection](#selection)
    - [Crossover](#crossover)
    - [Mutation](#mutation)
    - [Replacement](#replacement)
    - [Criteria of Termination](#criteria-of-termination)
    - [Parameter](#parameter)
    - [Genetic Algorithm - Main](#genetic-algorithm)
    - [Visualization of Results](#visualization-of-results)
    - [Helper Functions - Utils](#helper-functions)
    - [Pipeline](#pipeline)
- [Advanced Methods to implement later](#advanced-methods)
- [Expected Problems](#expected-problems)
- [Experiments](#experiments)
- [Performance Metrics](#performace-metrics)
- [Helpful Links](#links)

# Distribution of Tasks
| Deniz                                                     | Dilanur                          | 
|-----------------------------------------------------------|----------------------------------|
| 1. Generating(**in progress**)                            | 2. Encoding                      | 
| 3. Initialization                                         | 5. Selection                     | 
| 4. Fitness                                                | 6. Crossover                     | 
| Helper Fuctions(no rank do it when needed simultaneously) | 7. Mutation                      | 
| 10. Parameter                                             | 8. Replacement                   | 
| 12. Visualization                                         | 9. Termination                   | 
| 13. Pipeline                                              | 11. Algorithm - Main             | 
| 14. Fitness Sharing                                       | 16. Parameter Control and Tuning | 
| 15. Elitism                                               | 17. Hybrid Methods               | 
| 18. Adaptive Operations part 1                            | 19. Adaptive Operations part 2   | 

- [Random distribution link](https://wheeldecide.com/)  
- ![image](../../ExamScheduling/docs/image.png)

# Defining constant terms 
## Naming Convention
**Examples** : 
* POPULATION_SIZE, MUTATION_RATE - Screaming Snake case
* population_size, mutation_rate - Snake case
* populationSize, mutationRate - Camel case
* ...

# Comment Format
**Examples** : 
 
```java
  /**
* Returns an Image object that can then be painted on the screen. 
* The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
* argument is a specifier that is relative to the url argument. 
* <p>
* This method always returns immediately, whether or not the 
* image exists. When this applet attempts to draw the image on
* the screen, the data will be loaded. The graphics primitives 
* that draw the image will incrementally paint on the screen. 
*
* @param  url  an absolute URL giving the base location of the image
* @param  name the location of the image, relative to the url argument
* @return      the image at the specified URL
* @see         Image
*/
  ```

* ...


# Automatic Documentation Extension
**Examples** : 
* Doxygen
* ...

# Folder Structure 
**Example** :
 
```
 genetic_algorithm_project/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── yourcompany/
│   │               └── genetic/
│   │                   ├── GeneticAlgorithm.java      # Main Genetic Algorithm class
│   │                   ├── Population.java            # Population class
│   │                   ├── Individual.java            # Individual class
│   │                   ├── FitnessFunction.java       # Interface for fitness functions
│   │                   ├── Selection.java             # Selection methods
│   │                   ├── Crossover.java             # Crossover methods
│   │                   └── Mutation.java              # Mutation methods
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── yourcompany/
│                   └── genetic/
│                       ├── GeneticAlgorithmTest.java  # Test cases for Genetic Algorithm
│                       ├── PopulationTest.java        # Test cases for Population
│                       ├── IndividualTest.java        # Test cases for Individual
│                       ├── FitnessFunctionTest.java   # Test cases for Fitness Functions
│                       ├── SelectionTest.java         # Test cases for Selection methods
│                       ├── CrossoverTest.java         # Test cases for Crossover methods
│                       └── MutationTest.java          # Test cases for Mutation methods
│
├── lib/                                              # External libraries (if any)
│
├── docs/                                             # Documentation (if any)
│
├── resources/                                        # Configuration files or any additional resources
│
└── README.md                                         # Project documentation
└── general.md                                        # Project documentation(This File) 

```
* ...

# Functions to implement

## Generating data/input/constraints
This is required for chromose encoding step. Dataset should contain student, teacher, class, classroom information. TAU data can be used for generation ,but it is not enough.<br>
**Concept Terminology/Methods** : 
* Artificial data generation - Small
* Artificial data generation - Medium
* Artificial data generation - Large
* ...

## Encoding of chromosomes, genes, constraints
**Concept Terminology/Methods** :
* Binary Encoding
* Gray Encoding
* Octal Encoding
* Hexadecimal Encoding
* Real Value Encoding
* Hard/Soft Constraint encoding (?)
* ...

## Initialization of population  
**Concept Terminology/Methods** :
* Random
* Heuristic
* Hybrid
* ...

## Fitness Calculation
**Concept Terminology/Methods** :
* Punishment 
* Fitness Landscape
* ...

## Selection
**Concept Terminology/Methods** :
* Roulette Wheel Selection
* Tournament Selection
* Stochastic Universal Sampling
* Ranking Selection
* ...

## Crossover
**Concept Terminology/Methods** :
* Asexual/sexual/multi Crossover
* One Point Crossover
* Uniform Crossover
* ...

## Mutation
**Concept Terminology/Methods** :
* Constant/adaptive Mutation Rate
* Bitflip Mutation
* Random Mutation
* ...

## Replacement 
**Concept Terminology/Methods** :
* Age/ Fitness score based
* ...

## Criteria of Termination
**Concept Terminology/Methods** :
* #Maximum Generation
* #Maximum Time (in minutes)
* Ideal Fitness Score
* ...

## Parameter
**Concept Terminology/Methods** :
* Grid Search/Random Search/Racing
* Deterministic/adaptive/self-adaptive
* Population Size
* Mutation Rate
* Crossover Rate
* Crossover Point
* #Maximum Generation
* Termination Criteria
* ...

## Genetic Algorithm - Main
**Concept Terminology/Methods** :
* Combination of all required steps/methods
* ...

## Visualization of Results 
**Concept Terminology/Methods** :
* Terminal Output
* Advanced : Website Application(not essential)
* ...

**Outputs** : 
* Exam Scheduling Table(pretty table)
* Graphics(plotly)
* Constraint Chechklist
* ...

## Helper Functions - Utils
Common functions that can be used by all team players.<br>
**Concept Terminology/Methods** :
* Functions to mess time
* Functions to plot graphs
* Functions to string manupulation
* Functions to run automatic pipelines
* Functions to analyze, read, clean excel etc. formatted data
* ...

## Pipeline 
**Concept Terminology/Methods** :
* Automic run at night
* Automatic result file generation and saving to drive folder
* ...

# Advanced Methods to implement later 
**Examples** : 
* Parameter Control und Tuning : belongs to [Parameter](#parameter) chapter
* Adaptive Operations : belongs to all the operator chapters
* Hybrid Methods : belongs to all of these chapters
* Elitism : belongs to [Replacement](#replacement) chapter
* Fitness Sharing : belongs to [Fitness Calculation](#fitness-calculation) chapter 
* ...

# Expected Problems 
**Examples** : 
* Slow Convergence
* Early Convergence
* Time Complexity
* Memory Complexity
* Fine-Tuning of Parameters
* ...

# Experiments 
* Different paramaters
* Different methods of operators
* Different sizes of artificial input data 
* ...

# Performance Metrics
* Fulfillment of Hard Constraint
* Fulfillment of Soft Constraint
* Fitness Score
* Speed
* Complexity
* Robustness
* Balance between diversity and quality
* Algorithm should always reach a good/ideal solution 
* ...

# Helpful Links
* [Python Basic Genetic Algorithm](https://medium.com/@Data_Aficionado_1083/genetic-algorithms-optimizing-success-through-evolutionary-computing-f4e7d452084f)
* [Javadoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
* [Doxygen](https://www.doxygen.nl/manual/docblocks.html)
* ...
