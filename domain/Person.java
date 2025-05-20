package domain;

public class Person {
  private String name;
  private int draws;
  private int losses;
  private int wins;
  private int matches;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public int getWins() {
    return wins;
  }
  
  public void setWins(int wins) {
    this.wins = wins;
  }

  public int getLosses() {
    return losses;
  }
  
  public void setLosses(int losses) {
    this.losses = losses;
  }
  
  public int getDraws() {
    return draws;
  }
  
  public void setDraws(int draws) {
    this.draws = draws;
  }
  
  public int getMatches() {
    return matches;
  }
  
  public void setMatches(int matches) {
    this.matches = matches;
  }  
}