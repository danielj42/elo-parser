import java.util.*;
import java.math.*;
import java.io.*;
import domain.Person;
import domain.PersonComparator;
import domain.Result;
import java.text.DecimalFormat;

public class Eloparser {
  public static void main (String[] args)  {
    Eloparser eloparser = new Eloparser();
    eloparser.runProgram(args);
  }
  
  public void runProgram(String[] filenames) {
    List<Person> persons = new ArrayList<>();
    int tournaments = 0;
    double matchWins = 0.0;
    Integer totalMatches = 0;
    DecimalFormat df = new DecimalFormat("#.##");
    List<Integer> placings = new ArrayList<>();
       
    // Read text files
    List<List<String>> documents = readDocumentsFromFiles(filenames);
    
    // Iterate over documents
    for (List<String> document : documents) {
      for (String row : document) { // Iterate over rows in document
        if (row.substring(2,3).equals("-")) { // Tournament information row
          tournaments++;
          
          String currentDigit = "";
          String currentNumber = "";
          List<Integer> numbers = new ArrayList<>();
          
          for (int i = 0; i < row.length(); i++) {
            if (Character.isDigit(row.charAt(i))) {
              for (int x = i; x < row.length(); x++) {
                currentDigit = row.substring(x, x + 1);
                if (!Character.isDigit(row.charAt(x))) {
                  numbers.add(Integer.parseInt(currentNumber));
                  currentNumber = "";
                  i = x;
                  break;
                }
                currentNumber = currentNumber + currentDigit;
              }
            }
          }
          placings.add(numbers.get(numbers.size() - 1));
          continue;
        } else if (row.substring(0,7).equals("Initial")) { // Initial rank, bottom row of document
          break;
        } else {
          //Name and result of individual match
          String currentWord = "";
          String currentLetter = "";
          int counter = 1;
          String name = "";
          Result result = Result.UNDEFINED;

          for (int i = 2; i < row.length(); i++) { // Start after the match number and initial whitespace ("1 _")
            if (!Character.isDigit(row.charAt(i)) && Character.isAlphabetic(row.charAt(i))) { // Hits a letter
              for (int x = i; x < row.length(); x++) {
                currentLetter = row.substring(x, x + 1);
                
                //Hits whitespace
                if (currentLetter.equals(" ") || currentLetter.equals("\t")) {
                  counter++;
                  if (currentWord.equals("Bye")) {
                      result = Result.BYE;
                      i = row.length();
                      break;
                  }
                  if (counter >= 3) { // After two names check if word is a result
                    if (currentWord.equals("Win")) {
                      result = Result.WIN;
                      matchWins++;
                    } else if (currentWord.equals("Loss")) {
                      result = Result.LOSS;
                    } else if (currentWord.equals("Tie")) {
                      result = Result.DRAW;
                    }
                    if (result != Result.UNDEFINED) { // End iteration if a result is found
                      i = row.length();
                      break;
                    }
                  }
                  //Add word to full name
                  if (counter == 2) {
                    name = currentWord;
                  } else {
                    name = name + " " + currentWord;
                  }
                  currentWord = "";
                  i = x;
                  break;
                }
                
                // If no whitespace is hit, add current letter to word
                currentWord = currentWord + currentLetter;
              }
            }
          }
          
          if (result != Result.BYE) {
            totalMatches++;
            // Add result to list of persons
            int hit = 0;
            for (Person person : persons) {
              if (person.getName().equals(name)) {
                hit = 1;
                setResultInPerson(person, result);
              }
            }
            if (hit == 0) {
              Person newPerson = new Person();
              newPerson.setName(name);
              setResultInPerson(newPerson, result);       
              persons.add(newPerson);
            }
          }
        }
      }
    }
    
    Collections.sort(persons, new PersonComparator());
    
    // Determine placement statistics
    int trophies = 0;
    int total = 0;
    Integer median = getMedian(placings);
    double winPercentage = matchWins/totalMatches * 100; 
    for (Integer placing : placings) {
      if (placing == 1) {
        trophies ++;
      }
      total = total + placing;
    }
    double averagePlacing = total/tournaments;
    
    // Display on screen
    System.out.println("");
    for (Person player : persons) {
      System.out.println(player.getName() + ".......Matches: " + player.getMatches() + ", Wins: " + player.getWins() + ", Losses: " + player.getLosses() + ", Draws: " + player.getDraws());
    }
    
    System.out.println("\n\nTotal matches played: " + totalMatches);
    System.out.println("Match winpercentage: " + df.format(winPercentage));
    
    System.out.println("\nAmount of tournaments: " + tournaments);
    System.out.println("First places: " + trophies);
    System.out.println("Average placement: " + averagePlacing);
    System.out.println("Median placement: " + median);
  }
  
  void setResultInPerson(Person person, Result result) {
    if (result == Result.WIN) {
      person.setWins(person.getWins() + 1);
    } else if (result == Result.LOSS) {
      person.setLosses(person.getLosses() + 1);
    } else if (result == Result.DRAW) {
      person.setDraws(person.getDraws() + 1);
    } else {
      return;
    }
    person.setMatches(person.getMatches() + 1);
  }
  
  Integer getMedian(List<Integer> values) {
        Collections.sort(values);
        if (values.size() % 2 == 1)
            return values.get((values.size() + 1) / 2 - 1);
        else {
            int lower = values.get(values.size() / 2 - 1);
            int upper = values.get(values.size() / 2);
            return (lower + upper) / 2;
        }
    }
  
  List<List<String>> readDocumentsFromFiles(String[] filenames) {
    List<List<String>> documents = new ArrayList<>();
    for (String filename : filenames) {
      List<String> items = new ArrayList<>();
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
          items.add((line + System.lineSeparator()).toString());
          line = br.readLine();
        }
        documents.add(items);
        br.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
    return documents;
  }
}