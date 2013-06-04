package simseer.cosine;

/*
    SimSeer - a search engine for finding similar documents
    Copyright (C) 2013  Kyle Williams <kwilliams@psu.edu>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import simseer.cosine.DocVector;

import java.util.HashMap;

/**
 * This class calculates the cosine similarity between two documents.
 * Somewhat based on http://sujitpal.blogspot.com/2011/10/computing-document-similarity-using.html
 * 
 * The code works as follows:
 * 1. A HashMap representing the vocabulary is built for both languages
 * 2. A DocVector is created for each document and initialized with the vocabulary
 * 3. The weight of each token in the DocVector is updated based on the frequency in the document represented by that DocVector
 * 4. The DocVectors are normalized and the cosine similarity between the two is calculated
 */

public class CosineSimilarity {

    /**
     * Creates a new CosineSimilarity object.
     */
    public CosineSimilarity() {

    }

    /**
     * Calculates the similarity between two TermFreqVectors
     * @param vec1 the first TermFreqVector
     * @param vec2 the second TermFreqVector
     * @return the cosine similarity of the TermFreqVectors
     */
    public double cosineSimilarity(TermFreqVector vec1, TermFreqVector vec2) throws IOException {

        HashMap<String,Integer> terms  = new HashMap<String,Integer>();

        //Get all of the terms and term frequencies in the two vecors
        String[] termTexts1 = vec1.getTerms();
        String[] termTexts2 = vec2.getTerms();
        int[] termFreqs1 = vec1.getTermFrequencies();
        int[] termFreqs2 = vec2.getTermFrequencies();

        //Store the terms and their positions in a hashmap - this represents the vocabulary
        int pos = 0;
        for (String term : termTexts1) {
            terms.put(term, pos++);
        }
        for (String term : termTexts2) {
            if (!terms.containsKey(term)) {
                terms.put(term, pos++);
            }
        }

        //Create vectors representing the two documents
        DocVector dv1 = new DocVector(terms);
        DocVector dv2 = new DocVector(terms);

        //Set the entries in the two documents, i.e., the term weights in the document vectors
        for (int i = 0; i < termTexts1.length; i++) {
            dv1.setEntry(termTexts1[i], termFreqs1[i]);
        }
        for (int i = 0; i < termTexts2.length; i++) {
            dv2.setEntry(termTexts2[i], termFreqs2[i]);
        }

        //Normalize
        dv1.normalize();
        dv2.normalize();

        //Return the cosine similarity of the two document vectors
        return (dv1.vector.dotProduct(dv2.vector))/(dv1.vector.getNorm() * dv2.vector.getNorm());

    }
    
    /**
     * Calculates the cosine similarity between two documents.
     * @param d1 the first document
     * @param d2 the second document
     * @return the cosine similarity
     * @throws IOException
     */
    private double getCosineSimilarity(String d1, String d2) throws IOException{
    	
    	RAMDirectory ramDir = new RAMDirectory();
    	
    	//Index the full text of both documents
    	IndexWriter writer = new IndexWriter(ramDir, new StandardAnalyzer(Version.LUCENE_36), true, IndexWriter.MaxFieldLength.UNLIMITED);
        Document doc = new Document();
        doc.add(new Field("text", FileUtils.readFileToString(new File(d1), "UTF-8"), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new Field("text", FileUtils.readFileToString(new File(d2), "UTF-8"), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES));
        writer.addDocument(doc);
        writer.close();
        
        //Build a term vector for each document
        IndexReader RAMreader = IndexReader.open(ramDir);
        TermFreqVector doc1FreqVector = RAMreader.getTermFreqVector(0, "text");
        TermFreqVector doc2FreqVector = RAMreader.getTermFreqVector(1, "text");
        RAMreader.close();
        ramDir.close();
        
        //Return the cosine similarity of the term vectors
        return cosineSimilarity(doc1FreqVector, doc2FreqVector);
        
    }

    /**
     * Main class.
     * Returns the cosine similarity between two documents
     * @param args an array containing the filenames to compare
     */
    public static void main (String [] args) throws Exception{
        
        String file1 = args[0];
        String file2 = args[1];
        
        System.out.println(new CosineSimilarity().getCosineSimilarity(file1, file2));

    }
    
}
