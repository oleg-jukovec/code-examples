package ru.ozi_blog.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;

import ru.ozi_blog.task.calc.Calculator;
import ru.ozi_blog.task.calc.CalculatorDirector;
import ru.ozi_blog.task.calc.primitives.DoublePrimitive;
import ru.ozi_blog.task.exceptions.ArgumentCalculatorException;
import ru.ozi_blog.task.exceptions.BuildCalculatorException;
import ru.ozi_blog.task.exceptions.FileContentCalculatorException;
import ru.ozi_blog.task.exceptions.MemoryCalculatorException;
import ru.ozi_blog.task.exceptions.ProcessingCalculatorException;
import ru.ozi_blog.task.output.Errors;
import ru.ozi_blog.task.output.Printer;
import ru.ozi_blog.task.output.StringConstants;



/**
 * The class SolutionDemo is used for demonstration a solution for the task.
 * 
 * @author Zhukavets Aleh
 * 
 */

public class SolutionDemo {
	/**
	 * Input file must be in UTF-8 encoding
	 * 
	 * @param args
	 *            the name of an input file
	 */
	public static void main(String[] args) {
		/*
		 * the object will be used for a console output
		 */
		Printer<Double> output = new Printer<Double>();
		try {
			/*
			 * if the input file name is not specified, thrown the exception
			 */
			if (args.length == 0)
				throw new ArgumentCalculatorException();
			/*
			 * build one file name from args
			 */
			StringBuilder stringBuilder = new StringBuilder();
			for (String i : args) {
				stringBuilder.append(i);
				stringBuilder.append(StringConstants.SPACE.getString());
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			String fileName = new String(stringBuilder);
			/*
			 * read file data as the list of strings
			 */
			LinkedList<String> fileStrings = new LinkedList<String>();
			fileStrings.addAll((Files.readAllLines(Paths.get(fileName),
					StandardCharsets.UTF_8)));
			/*
			 * make a deque that can be used in a calculation
			 */
			Deque<String> calcDeque = fileStrings;
			/*
			 * get a calculator type for build
			 */
			String calculatorType = calcDeque.pop();
			/*
			 * build a new calculator object
			 */
			Calculator<Double> calculator = null;
			CalculatorDirector<Double> director = new CalculatorDirector<Double>(
					DoublePrimitive.instance());
			calculator = director.getCalculator(calculatorType);
			/*
			 * calculate and print a result to the console
			 */
			output.printAnswer(calculator.calculate(calcDeque));
		/*
		 * prints various errors
		 */
		} catch (ArgumentCalculatorException e) {
			output.printError(Errors.ARGUMENTS_NOT_FOUND);
		} catch (IOException e1) {
			output.printError(Errors.FILE_READING);
		} catch (FileContentCalculatorException e2) {
			output.printError(Errors.FILE_CONTENT);
		} catch (BuildCalculatorException e3) {
			output.printError(Errors.BUILD);
		} catch (ProcessingCalculatorException e4) {
			output.printError(Errors.CALCULATION);
		} catch (MemoryCalculatorException e4) {
			output.printError(Errors.MEMORY);
		} catch (Exception e5) {
			output.printError(Errors.UNKNOWN);
			e5.printStackTrace();
		}
	}
}