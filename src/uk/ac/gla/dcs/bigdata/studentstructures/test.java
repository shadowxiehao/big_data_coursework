package uk.ac.gla.dcs.bigdata.studentstructures;

import java.util.List;

import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.providedutilities.TextPreProcessor;

public class test {
	private transient static TextPreProcessor processor;
	public static void main(String[] args) {
		if (processor == null) processor = new TextPreProcessor();
		
		String str1 = "Facebook IPO: How big will it be and what’s the significance?";
		String str2 = "Facebook IPO: How does Facebook make its money?";
		List<String> l1 = processor.process(str1);
		List<String> l2 = processor.process(str2);
		System.out.println(l1);
		System.out.println(l2);
		System.out.println(TextDistanceCalculator.similarity(String.join(" ", l1), String.join(" ", l2)));
		System.out.println(TextDistanceCalculator.similarity(str1, str2));
	}
}

// Report: Facebook to file IPO next Wednesday
//Facebook: 1,000 new millionaires?
//Facebook IPO: How big will it be and what’s the significance?
//Report: Facebook to file for $5 billion IPO
//Facebook’s IPO: Putting it in context
//Facebook IPO: How does Facebook make its money?
//Facebook IPO: A look at what we learned from the filings
//Facebook’s leadership team: Silicon Valley’s ‘Young Guns’
//Facebook IPO: Who are Facebook’s shareholders?
// Facebook and the big IPO letdown