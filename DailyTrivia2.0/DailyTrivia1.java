/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */



import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class DailyTrivia1 {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dailytrivia(userquestion)";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
       
        // Prompt user to enter registration date
        System.out.println("Please enter your registration date (YYYY-MM-DD):");
        String registrationDateString = scanner.nextLine();
        
        while (registrationDateString == null){
            System.out.println("Please enter your registration date (YYYY-MM-DD):");
            registrationDateString = scanner.nextLine();
        }
        
        LocalDate registrationDate = LocalDate.parse(registrationDateString);
        LocalDate currentDate = LocalDate.now();
        
        Map<LocalDate, Integer> answeredQuestions = new HashMap<>();
        
        int numberOfQuestions = 10;

        // create an array for storing question
        String[] questions = new String[numberOfQuestions];

        // create an 2D array for storing choices
        String[][] options = new String[numberOfQuestions][];

        // create an anrray for storing correct answer's index
        int[] answers = new int[10];

        File triviaSample = new File("TriviaSample.txt");
        
        try {
            
            Scanner sc = new Scanner(triviaSample);


            while (sc.hasNextLine()) {
                for(int i = 0; i < numberOfQuestions; i++) {
                    // store the question into the array
                    questions[i] = sc.nextLine();

                    // skip 3 line
                    for(int j = 0; j < 3 && sc.hasNextLine(); j++) {
                        sc.nextLine();
                    }
                }
            }
            sc.close();

            Scanner sc2 = new Scanner(triviaSample);

            while (sc2.hasNextLine()) {
                for(int i = 0; i < numberOfQuestions; i++) {

                    // skip for the question line
                    sc2.nextLine();

                    // store the choices into a string
                    // eg. 4%,10%,25%,50%
                    String choice = sc2.nextLine();

                    // store the choices into the array
                    // eg. {"4%", "10%", "25%", "50%"}
                    String temporaryArr[] = choice.split(",");

                    // store the choices into 2D array
                    // eg. {
                    //      {"4%", "10%", "25%", "50%"},
                    //      {"500 million", "900 million", "1.6 billion", "5 billion"}
                    //      ...
                    //     }
                    options[i] = temporaryArr;

                    // skip 2 line
                    for(int z = 0; z < 2 && sc2.hasNextLine(); z++) {
                        sc2.nextLine();
                    }
                }
            }

            sc2.close();

            Scanner sc3 = new Scanner(triviaSample);

            while (sc3.hasNextLine()) {
                for(int i = 0; i < numberOfQuestions; i++) {

                    // skip 2 line
                    for (int j = 0; j < 2; j++) {
                        sc3.nextLine();
                    }

                    // store the answer
                    String answer = sc3.nextLine();

                    // if the answer is equal to one of the choices
                    // then save the index into the array
                    for (int z = 0; z < options[i].length; z++) {
                        if(answer.equals(options[i][z])) {
                            answers[i] = z;
                        }
                    }

                    // skip 1 line
                    if(sc3.hasNextLine()) {
                        sc3.nextLine();
                    }
                }
            }

            sc3.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int totalMarks = 0;
        int currentQuestionIndex = 0;
        
        // Calculate the current day's question index
        if (!currentDate.isBefore(registrationDate)) {
            currentQuestionIndex = (int) ChronoUnit.DAYS.between(registrationDate, currentDate);
        }
        
        for (int i=currentQuestionIndex; i < questions.length; i++) {
        LocalDate currentQuestionDate = registrationDate.plusDays(i);

        if (currentQuestionDate.isEqual(currentDate)) {
            int attempts = answeredQuestions.getOrDefault(currentQuestionDate, 0) + 1;
            answeredQuestions.put(currentQuestionDate, attempts);
            
                // Fetch question, options, and correct answer based on the current index
                String question = questions[i];
                String [] answerOptions = options[i];
                int correctAnswerIndex = answers[i];

                System.out.println("\nDay " +  (i+1)  + " Trivia (Attempt #" + attempts + ")");
                System.out.println(question);
                displayAnswerOptions(answerOptions);
                System.out.println("=======================================================");
                int userAnswerIndex = getUserAnswer(scanner, answerOptions);
                System.out.println("=======================================================");

                if (userAnswerIndex == correctAnswerIndex) {
                    totalMarks += 2;
                    System.out.println("Congratulations! You've earned 2 points. You now have " + totalMarks + " points.");
                } else if (userAnswerIndex != -1) {
                    String correctAnswer = answerOptions[correctAnswerIndex];

                    shuffleAnswerOptions(answerOptions);
                    int newCorrectIndex = getNewCorrectIndex(answerOptions, correctAnswer);
                    
                    System.out.println("Whoops, that doesn't look right, try again!\n");
                    
                    System.out.println("\nDay " +  (i+1)  + " Trivia (Attempt #" +(attempts+1) + ")");
                    System.out.println(question);
                    displayAnswerOptions(answerOptions);
                    System.out.println("=======================================================");
                    userAnswerIndex = getUserAnswer(scanner, answerOptions);
                    System.out.println("=======================================================");

                    if (userAnswerIndex == newCorrectIndex) {
                        totalMarks += 1;
                        System.out.println("Congratulations! You answered it correctly. \nYou have been awarded 1 point, you now have " + totalMarks + " points.");
                    } else {
                        System.out.println("Your answer is still incorrect, the correct answer is: \n" +  correctAnswer);
                        System.out.println("Total marks achieved so far: " + totalMarks);
                      }
                    
                } else {
                    System.out.println("You chose not to answer this question.");
                }
            }
        
        }
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Your existing code here

            // Example of inserting user answers into the database
            // Assuming user_id and question_id are known
            int userId = 1; // Example user ID
            int questionId = 1; // Example question ID
            int attempt = 1; // Example attempt number
            String userAnswer = "A"; // Example user's answer
            int marks = 2; // Example marks earned

            // SQL statement to insert user's answer into the database
            String insertQuery = "INSERT INTO user_answers (user_id, question_id, attempt, user_answer, marks) VALUES (?, ?, ?, ?, ?)";
            
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, questionId);
            preparedStatement.setInt(3, attempt);
            preparedStatement.setString(4, userAnswer);
            preparedStatement.setInt(5, marks);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }    
    }
      
    // Display answer options
    private static void displayAnswerOptions(String[] answerOptions) {
    for (int i = 0; i < answerOptions.length; i++) {
        char option = (char) ('A' + i);
        System.out.println(option + ". " + answerOptions[i]);
    }
}

    // Shuffle answer options
    private static void shuffleAnswerOptions(String[] answerOptions) {
        List<String> optionsList = Arrays.asList(answerOptions);
        Collections.shuffle(optionsList);
        optionsList.toArray(answerOptions);
    }

    // Get user's answer index
    private static int getUserAnswer(Scanner scanner, String[] answerOptions) {
        System.out.print("Enter answer (A/B/C/D): ");
        char input = scanner.next().toUpperCase().charAt(0);
        int choice = input - 'A';
        return (choice >= 0 && choice < answerOptions.length) ? choice : -1;
    }

    // Get new correct answer index after shuffling
    private static int getNewCorrectIndex(String[] answerOptions, String correctAnswer) {
        for (int i = 0; i < answerOptions.length; i++) {
            if (answerOptions[i].equals(correctAnswer)) {
                return i;
            }
        }
        return -1;
    }
}