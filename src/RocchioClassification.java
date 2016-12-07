import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by LeafChernchaosil on 12/4/16.
 */
public class RocchioClassification {

    private CentralIndex mCI;
    private CentralIndex queryCI;
    // HashMap that maps Class --> [ term ---> weight ]
    HashMap<String, TreeMap<String, Double>> classToCentroidHM;

    public RocchioClassification() {
        classToCentroidHM = new HashMap<String, TreeMap<String, Double>>();
    }

    public void setCentralIndex(CentralIndex pCI) {
        mCI = pCI;
    }

    public void setQueryIndex(CentralIndex pCI) {
        queryCI = pCI;
    }

    /**
     * Classify all the training data
     */
    public void classifyTrainingData() {
        // Get ArrayList of class names
        ArrayList<String> classes = mCI.getClassName();

        // For each of the class, Ex: Hamilton
        for (String eachClass : classes) {

            // A TreeMap that maps a term to the weight, this is the vector
            TreeMap<String, Double> termToWeightVector = new TreeMap<String, Double>();

            // Get all documents in class
            ArrayList<String> documentsInClassArr = mCI.getDocInClass(eachClass);

            // For each of the document in class, Ex: Doc1
            for (String eachDoc : documentsInClassArr) {
//                System.out.println("Doc: " + eachDoc);
                // Get all the terms in the document
                ArrayList<String> termsInDoc = mCI.getTermInDoc(eachDoc);
                // Get the Ld
                double Ld = this.returnLd(termsInDoc, eachDoc, mCI);
//                System.out.println("Ld: " + Ld);

                // For each term in the document, calculate W(d,t) and use it as the components of the vector
                // The W(d,t) is then normalized by L(d)
                for (String eachTerm : termsInDoc) {
                    int termFreqInDoc = mCI.getTermFreq(eachDoc, eachTerm); // tf(t,d)
                    double weightOfTerm = 1 + Math.log(termFreqInDoc); // W(d,t)
                    double normalizedWeight = (weightOfTerm / Ld);
                    this.putTermInVector(termToWeightVector, eachTerm, normalizedWeight);
                }
//                this.printVector(termToWeightVector);
            }

            int numOfDocsInClass = documentsInClassArr.size();
            // Find the centroid of the class by dividing vector by number of documents
            for (Map.Entry<String, Double> eachEntry : termToWeightVector.entrySet()) {
                double result = eachEntry.getValue() / numOfDocsInClass;
                eachEntry.setValue(result);
            }

            // Put the class ---> centroid
            classToCentroidHM.put(eachClass, termToWeightVector);
//            this.printVector(termToWeightVector);
        }
    }

    public void classifyUnknownDocument() {
        ArrayList<String> unknownClass = queryCI.getClassName();
        for (String unknownClassName : unknownClass) {
//            System.out.println("UnknownClassName: " + unknownClassName);
            ArrayList<String> documentsInUnknownClass = queryCI.getDocInClass(unknownClassName);
            for (String eachUnknownDocument : documentsInUnknownClass) {
                String classOfDocument = returnClassOfDocument(eachUnknownDocument);
                System.out.println(eachUnknownDocument + ": " + classOfDocument + "\n");
            }
        }
    }
    /**
     * Given an unknown document name, return the class
     * @param pUnknownDocName - Unknown document name
     * @return The class the document belongs
     */
    public String returnClassOfDocument(String pUnknownDocName) {
        // Have to Construct a normalized vector for the unknown document first

        // A TreeMap that maps a term to the weight, this is the vector
        TreeMap<String, Double> unknownDocVector = new TreeMap<String, Double>();

        // Get all the terms in the document
        ArrayList<String> termsInUnknownDoc = queryCI.getTermInDoc(pUnknownDocName);
        double Ld = returnLd(termsInUnknownDoc, pUnknownDocName, queryCI);

        // For each term in the document, calculate W(d,t) and use it as the components of the vector
        // The W(d,t) is then normalized by L(d)
        for (String eachTerm : termsInUnknownDoc) {
            int termFreqInDoc = queryCI.getTermFreq(pUnknownDocName, eachTerm); // tf(t,d)
            double weightOfTerm = 1 + Math.log(termFreqInDoc); // W(d,t)
            double normalizedWeight = (weightOfTerm / Ld);
            this.putTermInVector(unknownDocVector, eachTerm, normalizedWeight);
        }
//        this.printVector(unknownDocVector);

        TreeMap<Double, String> distanceToClassTM = new TreeMap<>();
        // Creating temp classToCentroidHM;
        HashMap<String, TreeMap<String, Double>> tempClassToCentroidHM = new HashMap<String, TreeMap<String, Double>>();
        for (Map.Entry<String, TreeMap<String, Double>> eachCentroid : classToCentroidHM.entrySet()) {
            TreeMap<String, Double> newTempTM = new TreeMap<>();
            for (Map.Entry<String, Double> eachTmEntry : eachCentroid.getValue().entrySet()) {
                newTempTM.put(eachTmEntry.getKey(), eachTmEntry.getValue());
            }
            tempClassToCentroidHM.put(eachCentroid.getKey(), newTempTM);
        }

        // Now that we have the normalized vector of the unknown document
        // For each of the centroid of each class
        for (Map.Entry<String, TreeMap<String, Double>> eachCentroid : tempClassToCentroidHM.entrySet()) {
            // Get the vector of the class
            TreeMap<String, Double> currentCentroidVector = eachCentroid.getValue();

            // For each of the element of Centroid Vector,
            // Check if the term exists in the vector of the unknown document
            //   If it does, substract it, if not, do nothing
            for (Map.Entry<String, Double> centroidVectorComponent : currentCentroidVector.entrySet()) {
                String eachTerm = centroidVectorComponent.getKey();
                if (unknownDocVector.containsKey(eachTerm)) {
                    double result = centroidVectorComponent.getValue() - unknownDocVector.get(eachTerm);
                    centroidVectorComponent.setValue(result);
                }
            }

//            this.printVector(currentCentroidVector);

            // Run through Vector of the unknown document,
            //  Find the element that do not exist in Vector of the current class
            //   Insert that element to the centroid vector
            for (Map.Entry<String, Double> documentVector : unknownDocVector.entrySet()) {
                String eachTerm = documentVector.getKey();
                if (!currentCentroidVector.containsKey(eachTerm)) {
                    currentCentroidVector.put(eachTerm, documentVector.getValue());
                }
            }

//            this.printVector(currentCentroidVector);

            double sum = 0;
            for (Map.Entry<String, Double> eachComponent : currentCentroidVector.entrySet()) {
                sum += (eachComponent.getValue() * eachComponent.getValue());
            }
            double distance = Math.sqrt(sum);
//            System.out.println("sum: " + sum);
//            System.out.println("Distance: " + distance);
//            System.out.println("For class: " + eachCentroid.getKey() + "\n");
            // Map the distance value to the class into TreeMap (distance(key) is sorted) upon inserting
            distanceToClassTM.put(distance, eachCentroid.getKey());
        }
//        System.out.println("UnknownDoc " + pUnknownDocName + " belongs to " + distanceToClassTM.firstEntry().getValue() + "\n");
        return distanceToClassTM.firstEntry().getValue();
    }

    /**
     * Return Ld given ArrayList of terms in document and current document
     * @param pTermsInDoc - ArrayList of Terms in documment
     * @param pDocument - Current document
     * @return Ld
     */
    private double returnLd(ArrayList<String> pTermsInDoc, String pDocument, CentralIndex pCI) {
        // Calculate Ld
        double sumOfWeightSquared = 0;
        for (String eachTerm : pTermsInDoc) {
            if (eachTerm.length() > 0 && !eachTerm.equals(" ")) {
                // tf(t,d)
                int termFreqInDoc = pCI.getTermFreq(pDocument, eachTerm);
                // W(d,t) = 1 + ln( tf(t,d) )
                double weightOfTerm = 1 + Math.log(termFreqInDoc);
                sumOfWeightSquared += (weightOfTerm * weightOfTerm);
//                System.out.println("Term: " + eachTerm);
//                System.out.println("tf(t,d): " + termFreqInDoc);
//                System.out.println("W(t,d): " + weightOfTerm);
//                System.out.println("sumSquared: " + sumOfWeightSquared);
            }
        }
//        System.out.println("sqrt of sum: " + Math.sqrt(sumOfWeightSquared));
        return Math.sqrt(sumOfWeightSquared);
    }

    /**
     * Put the term in the vector along with the normalized weight
     * @param pVector - Vector
     * @param pTerm - Term
     * @param pNormalizedWeight - Normalized weight
     */
    private void putTermInVector(TreeMap<String, Double> pVector, String pTerm, double pNormalizedWeight) {
        if (!pTerm.equals(" ") && pTerm.length() > 0) {
            // If the component already existed for the term, just add to it
            if (pVector.containsKey(pTerm)) {
                // Sum of the component of vectors
                double sum = pVector.get(pTerm) + pNormalizedWeight;
                // Put in new balue
                pVector.put(pTerm, sum);
            }
            else { // If not, set the new value
                pVector.put(pTerm, pNormalizedWeight);
            }
        }
    }

    private void printVector(TreeMap<String, Double> pVector) {
        System.out.print("< ");
        for (Map.Entry<String, Double> eachEntry : pVector.entrySet()) {
            System.out.print(eachEntry.getKey() + ":" + eachEntry.getValue() + ", ");
        }
        System.out.println(">\n");
    }
}
