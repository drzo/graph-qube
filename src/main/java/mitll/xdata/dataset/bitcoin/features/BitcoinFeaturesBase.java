/*
 * Copyright 2013-2016 MIT Lincoln Laboratory, Massachusetts Institute of Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mitll.xdata.dataset.bitcoin.features;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import mitll.xdata.dataset.bitcoin.binding.BitcoinBinding;
import mitll.xdata.db.DBConnection;
import mitll.xdata.scoring.FeatureNormalizer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: go22670
 * Date: 7/11/13
 * Time: 8:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinFeaturesBase {
  private static final Logger logger = Logger.getLogger(BitcoinFeaturesBase.class);

  public static final int MIN_TRANSACTIONS = 10;

  //  private static final boolean LIMIT = false;
  private static final int BITCOIN_OUTLIER = 25;
  //  private static final int USER_LIMIT = 10000000;
//  private static final int MIN_DEBITS = 5;
//  private static final int MIN_CREDITS = 5;
//  private static final List<Double> EMPTY_DOUBLES = Arrays.asList(0d, 0d);
  private static final int SPECLEN = 100;//
  private static final int NUM_STANDARD_FEATURES = 10;
  //public static final String BITCOIN_IDS_TSV = "bitcoin_ids.tsv";
  private static final String BITCOIN_RAW_FEATURES_TSV = "bitcoin_raw_features.tsv";
  private static final String BITCOIN_FEATURES_STANDARDIZED_TSV = "bitcoin_features_standardized.tsv";
  public static final String USERS = "users";
  // private static final boolean USE_SPECTRAL_FEATURES = true;
  // double specWeight = 1.0;
  // private final double statWeight = 15.0;
  // private final double iarrWeight = 30.0;
  // private final double ppWeight   = 20.0;
  private final boolean useSpectral = false;

  private static final int HOUR_IN_MILLIS = 60 * 60 * 1000;
//  private static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

  private enum PERIOD {HOUR, DAY, WEEK, MONTH}

//  private final PERIOD period = PERIOD.DAY; // bin by day for now

/*  public BitcoinFeaturesBase(String h2DatabaseFile, String writeDirectory, String datafile) throws Exception {
    this(new H2Connection(h2DatabaseFile, 38000000), writeDirectory, datafile, false);
  }*/

  /**
   * # normalize features
   * m, v = mean(bkgFeatures, 2), std(bkgFeatures, 2)
   * mnormedFeatures  = accountFeatures - hcat([m for i = 1:size(accountFeatures, 2)]...)
   * mvnormedFeatures = mnormedFeatures ./ hcat([v for i = 1:size(accountFeatures, 2)]...)
   * weights          = [ specWeight * ones(length(m) - 10);
   * statWeight * ones(2);
   * iarrWeight * ones(2);
   * statWeight * ones(2);
   * iarrWeight * ones(2);
   * (args["graph"] ? ppWeight : 0.0) * ones(2)
   * ]
   * weightedmv       = mvnormedFeatures .* weights
   * <p>
   * Writes out four files -- pairs.txt, bitcoin_features.tsv, bitcoin_raw_features.tsv, and bitcoin_ids.tsv
   *
   * @param connection
   * @param datafile   original flat file of data - transactions!
   * @throws Exception
   * @see #main(String[])
   */
/*  private BitcoinFeaturesBase(DBConnection connection, String writeDirectory, String datafile, boolean useSpectralFeatures) throws Exception {
    long then = System.currentTimeMillis();
    this.useSpectral = useSpectralFeatures;
    // long now = System.currentTimeMillis();
    // logger.debug("took " +(now-then) + " to read " + transactions);
    logger.debug("reading users from db " + connection);

    Collection<Integer> users = getUsers(connection);

    String pairsFilename = writeDirectory + "pairs.txt";

    writePairs(users, datafile, pairsFilename);

    Map<Integer, UserFeatures> transForUsers = getTransForUsers(datafile, users);

    writeFeatures(connection, writeDirectory, then, users, transForUsers);
  }*/

  /**
   * @param connection
   * @param writeDirectory
   * @param then
   * @param users
   * @param transForUsers
   * @throws Exception
   * @see BitcoinFeaturesUncharted#writeFeatures(DBConnection, String, long, Collection, Map)
   */
  Set<Long> writeFeatures(DBConnection connection, String writeDirectory, long then,
                          Collection<Long> users,
                          Map<Long, UserFeatures> transForUsers) throws Exception {
    long now = System.currentTimeMillis();
    logger.debug("writeFeatures took " + (now - then) + " to read " + transForUsers.size() + " user features");

    //BufferedWriter writer = new BufferedWriter(new FileWriter("bitcoin_features.tsv"));
    BufferedWriter rawWriter = new BufferedWriter(new FileWriter(new File(writeDirectory, BITCOIN_RAW_FEATURES_TSV)));
    //BufferedWriter idsWriter = new BufferedWriter(new FileWriter(BITCOIN_IDS_TSV));
    BufferedWriter standardFeatureWriter = new BufferedWriter(new FileWriter(new File(writeDirectory, BITCOIN_FEATURES_STANDARDIZED_TSV)));

    // write header liner
    //  idsWriter.write("user\n");

    //writeHeader(writer);
    writeHeader(rawWriter);
    writeHeader(standardFeatureWriter);

    List<Features> features = new ArrayList<Features>();
    Map<Long, Features> userToFeatures = new TreeMap<>();

    // TODO - change size if we add spectral features
    Map<Long, Integer> userToIndex = new HashMap<>();

    populateUserToFeatures(users, transForUsers, features, userToFeatures, userToIndex);

    logger.debug("writeFeatures populateUserToFeatures " + userToFeatures.size() + " user features");

    // copy features into a matrix

    double[][] standardizedFeatures = getStandardizedFeatures(features);
    // getStandardizationStats();

    writeFeaturesToFiles(rawWriter, standardFeatureWriter, userToFeatures, userToIndex, standardizedFeatures);

    writeFeaturesToDatabase(connection, userToFeatures, userToIndex, standardizedFeatures);

    //writer.close();
    rawWriter.close();
    // idsWriter.close();
    standardFeatureWriter.close();

    connection.closeConnection();

    return userToIndex.keySet();
  }

  /**
   * Calculate features for each user from the raw features in UserFeatures
   *
   * @param users
   * @param transForUsers
   * @param features
   * @param userToFeatures
   * @param userToIndex
   * @see #writeFeatures(DBConnection, String, long, Collection, Map)
   */
  private void populateUserToFeatures(Collection<Long> users,
                                      Map<Long, UserFeatures> transForUsers,
                                      List<Features> features,
                                      Map<Long, Features> userToFeatures,
                                      Map<Long, Integer> userToIndex) {
    int skipped = 0;
    int count = 0;
    logger.info("populateUserToFeatures checking " + transForUsers.size() + " against " + users.size() + " users");
    for (Long user : users) {
      // logger.debug("user " + user);
      Features featuresForUser = getFeaturesForUser(transForUsers, user);
      if (featuresForUser != null) {
        features.add(featuresForUser);
 /*       if (count < 10) {
          logger.debug("mapping " + user + " to " + count + " features " + featuresForUser);
        }*/
        userToIndex.put(user, count++);
        userToFeatures.put(user, featuresForUser);
      } else {
        skipped++;
      }
    }
    logger.info("populateUserToFeatures skipped " + skipped + " out of " + users.size() + " users who had less than " +
        MIN_TRANSACTIONS +
        " credits and less than " +
        MIN_TRANSACTIONS +
        " debits -> " + userToFeatures.size());
  }

  private void getStandardizationStats() {
    // normalize mean and variance

    //Features firstFeature = features.get(0);
    //int numFeatures = firstFeature.other.length;
    //DescriptiveStatistics[] summaries = getSummaries(features, numFeatures);

    // TODO : finish adding spectral features -- need weights, need to add mean/std for spectral features
    // double[] weightVector = new double[useSpectralFeatures ? 160 : 10];
/*
    double[] weightVector = new double[] {
        statWeight, statWeight, iarrWeight, iarrWeight, statWeight, statWeight, iarrWeight, iarrWeight, ppWeight, ppWeight };
*/

/*
    if (useSpectralFeatures) {
      Arrays.fill(weightVector,specWeight);
    }
    System.arraycopy(weightVector2, 0, weightVector, (useSpectralFeatures ? 150 : 0), weightVector2.length);
*/

/*    double[] means = new double[numFeatures];
    double[] stds = new double[numFeatures];

    // calculate means and standard deviations
    for (int i = 0; i < numFeatures; i++) {
      means[i] = summaries[i].getMean();
      stds[i] = summaries[i].getStandardDeviation();
      logger.debug("feature " + i + " " + means[i] + " std " + stds[i] + " num " + summaries[i].getN());
    }*/
  }

  private void writeFeaturesToFiles(BufferedWriter rawWriter, BufferedWriter standardFeatureWriter,
                                    Map<Long, Features> userToFeatures,
                                    Map<Long, Integer> userToIndex,
                                    double[][] standardizedFeatures) throws IOException {
    Features next = userToFeatures.values().iterator().next();
    int numFeatures = next.other.length;

    int j = 0;
    for (Map.Entry<Long, Features> userFeatPair : userToFeatures.entrySet()) {
      Features value = userFeatPair.getValue();
      double[] featureVector = value.other;
      long id = userFeatPair.getKey();
      // writer.write(id + "\t");
      rawWriter.write(id + "\t");
      //idsWriter.write(id + "\n");
      standardFeatureWriter.write(id + "\t");

      //if (useSpectralFeatures) {
      // TODO write out features, maybe to a separate file?
      // }
      Integer userIndex = userToIndex.get(id);

      double[] standardizedFeature = standardizedFeatures[userIndex];

		  /*      if (id < 10) {
        logger.debug("user " + id + " index " + userIndex + " features " + value + " vs " + getDoubles(standardizedFeature));
      }*/
      for (int i = 0; i < numFeatures; i++) {
        double v = featureVector[i];
        //      double finalValue = ((v - means[i]) / stds[i]) * weightVector[i];
        String separator = (i == numFeatures - 1) ? "\n" : "\t";
        //    writer.write(finalValue + separator);
        rawWriter.write(v + separator);

        double standardizedValue = standardizedFeature[i];
        standardFeatureWriter.write(standardizedValue + separator);

        if (j++ % 10000 == 0) {
          //    writer.flush();
          rawWriter.flush();
          standardFeatureWriter.flush();
        }
      }
    }
  }

  /**
   * @param dbConnection
   * @param userToFeatures
   * @param userToIndex
   * @param standardizedFeatures
   * @throws Exception
   * @see BitcoinFeaturesBase#writeFeatures(DBConnection, String, long, Collection, Map)
   */
  private void writeFeaturesToDatabase(DBConnection dbConnection,
                                       Map<Long, Features> userToFeatures,
                                       Map<Long, Integer> userToIndex,
                                       double[][] standardizedFeatures) throws Exception {
    logger.info("writeFeaturesToDatabase " + userToFeatures.size() + " users.");

    Connection connection = dbConnection.getConnection();
    new FeaturesSql().createUsersTable(connection);
    PreparedStatement statement;

    String[] columnLabels = {"USER", "CREDIT_MEAN", "CREDIT_STD",
        "CREDIT_INTERARR_MEAN", "CREDIT_INTERARR_STD",
        "DEBIT_MEAN", "DEBIT_STD",
        "DEBIT_INTERARR_MEAN", "DEBIT_INTERARR_STD",
        "PERP_IN", "PERP_OUT"};
    String columnLabelText = "(" + StringUtils.join(columnLabels, ", ") + ")";

    int numFeatures = columnLabels.length - 1;

    for (Map.Entry<Long, Features> userFeatPair : userToFeatures.entrySet()) {
      long id = userFeatPair.getKey();
      Integer userIndex = userToIndex.get(id);
      double[] standardizedFeature = standardizedFeatures[userIndex];

      String featValueText = "(" + id + ", ";
      for (int i = 0; i < numFeatures; i++) {
        double d = standardizedFeature[i];
        if (Double.isNaN(d)) d = 0;
        if (i != numFeatures - 1) {
          featValueText += Double.toString(d) + ", ";
        } else {
          featValueText += Double.toString(d) + ")";
        }
      }

      String sqlInsertVector = "insert into " +
          USERS +
          " " + columnLabelText + " values " + featValueText;
      statement = connection.prepareStatement(sqlInsertVector);
      statement.executeUpdate();
      statement.close();
    }

    //   logger.info("writeFeaturesToDatabase - alter users table");

    // Insert default type into table (to possibly be overwritten by clustering output)
/*    String sqlInsertType = "alter table USERS add TYPE int not null default(1);";
    statement = connection.prepareStatement(sqlInsertType);
    statement.executeUpdate();
    statement.close();*/

    connection.close();
  }

  public void pruneUsers(DBConnection dbConnection, Set<Long> toRemove) {
    Connection connection = dbConnection.getConnection();
    try {
      PreparedStatement statement =
          connection.prepareStatement("delete from " + FeaturesSql.USERS + " where USER=?");
      for (Long id : toRemove) {
        statement.setLong(1, id);
        int i = statement.executeUpdate();
        if (i != 1) logger.warn("huh? didn't delete " + id + " from users.");
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static String getDoubles(double[] arr) {
    String val = "";
    for (double d : arr) val += d + " ";
    return val;
  }

  private double[][] getStandardizedFeatures(List<Features> features) {
    double[][] featureMatrix = new double[features.size()][NUM_STANDARD_FEATURES];
    int i = 0;
    for (Features feature : features) {
      double[] dest = featureMatrix[i++];
      if (feature.other == null) logger.error("huh? feature vector is null");
      System.arraycopy(feature.other, 0, dest, 0, NUM_STANDARD_FEATURES);
    }

    for (int j = 0; j < NUM_STANDARD_FEATURES; j++) { //num_feats = 10 for our raw bitcoin features
      if (j == 4) {//: #change polarity for mean debits
        for (int k = 0; k < features.size(); k++) {
          featureMatrix[k][j] = Math.log(1 + Math.abs(featureMatrix[k][j]));
        }
      } else if ((j == 8) | (j == 9)) { //#perplexity metric has min val = 1
        for (int k = 0; k < features.size(); k++) {
          featureMatrix[k][j] = Math.log(featureMatrix[k][j]);
        }
      } else {
        for (int k = 0; k < features.size(); k++) {
          featureMatrix[k][j] = Math.log(1 + Math.abs(featureMatrix[k][j]));
        }
      }
    }

    double lowerPercentile = 0.025;
    double upperPercentile = 0.975;
    FeatureNormalizer normalizer = new FeatureNormalizer(featureMatrix, lowerPercentile, upperPercentile);
    return normalizer.normalizeFeatures(featureMatrix);
  }

  private void writeHeader(BufferedWriter writer) throws IOException {
    writer.write("user\t");

    writer.write("credit_mean\tcredit_std\t");
    writer.write("credit_interarr_mean\tcredit_interarr_std\t");
    writer.write("debit_mean\tdebit_std\t");
    writer.write("debit_interarr_mean\tdebit_interarr_std\t");
    writer.write("perp_in\tperp_out\n");
  }

/*
  private DescriptiveStatistics[] getSummaries(List<Features> features, int numFeatures) {
    DescriptiveStatistics[] summaries = new DescriptiveStatistics[numFeatures];

    for (int i = 0; i < numFeatures; i++) {
      summaries[i] = new DescriptiveStatistics();
    }
    for (Features featureVector : features) {
      for (int i = 0; i < numFeatures; i++) {
        summaries[i].addValue(featureVector.other[i]);
      }
    }
    return summaries;
  }
*/

  /**
   * 160 features
   * <p>
   * *    name         = $nms
   * credit_spec  = $credit_spec    x50
   * debit_spec   = $debit_spec     x50
   * merge_spec   = $merge_spec     x50
   * credit_stats = $credit_stats
   * credit_iarr  = $credit_iarr
   * debit_stats  = $debit_stats
   * debit_iarr   = $debit_iarr
   * p_in         = $p_in           perp
   * p_out        = $p_out          perp
   *
   * @param transForUsers
   * @param user
   * @see #populateUserToFeatures(Collection, Map, List, Map, Map)
   */
  private Features getFeaturesForUser(Map<Long, UserFeatures> transForUsers, Long user) {
    UserFeatures stats = transForUsers.get(user);

    if (stats == null) {
      //logger.debug("no transactions for " + user + " in "  + transForUsers.keySet().size() + " keys.");
      return null;
    }

    double[] sfeatures = new double[150]; // later 160
    double[] features = new double[NUM_STANDARD_FEATURES]; // later 160
    if (stats.isValid()) {
      stats.calc();

      // TODO get this going later
      int i = 0;
      if (useSpectral) {
        float[] creditSpec = stats.getCreditSpec();
        for (float aCreditSpec : creditSpec) sfeatures[i++] = aCreditSpec;
        float[] debitSpec = stats.getDebitSpec();
        for (float aDebitSpec : debitSpec) sfeatures[i++] = aDebitSpec;
        float[] mergeSpec = stats.getMergeSpec();
        for (float aMergeSpec : mergeSpec) sfeatures[i++] = aMergeSpec;
      }

      i = 0;

      List<Double> creditMeanAndStd = stats.getCreditMeanAndStd();
      List<Double> creditInterarrivalTimes = stats.getCreditInterarrMeanAndStd();
      List<Double> debitMeanAndStd = stats.getDebitMeanAndStd();
      List<Double> debitInterarrivalTimes = stats.getDebitInterarrMeanAndStd();

      double inPerplexity = stats.getInPerplexity();
      double outPerplexity = stats.getOutPerplexity();

      features[i++] = creditMeanAndStd.get(0);
      features[i++] = creditMeanAndStd.get(1);

      features[i++] = creditInterarrivalTimes.get(0);
      features[i++] = creditInterarrivalTimes.get(1);

      features[i++] = debitMeanAndStd.get(0);
      features[i++] = debitMeanAndStd.get(1);

      features[i++] = debitInterarrivalTimes.get(0);
      features[i++] = debitInterarrivalTimes.get(1);

      features[i++] = inPerplexity;
      features[i++] = outPerplexity;
    } else {
      //  logger.debug("\tuser " +user+ " is not valid");
      //features = null;
      return null;
    }

    return new Features(sfeatures, features);
  }

  private static class Features {
    final double[] spectral;
    final double[] other;  // the non-spectral 10 features

    Features(double[] spectral, double[] other) {
      this.spectral = spectral;
      this.other = other;
    }

    public String toString() {

      return "Features " + getDoubles(other);
    }
  }

  /**
   * TODO : accumulate 158/160 dim feature vector:
   *
   *    name         = $nms
   credit_spec  = $credit_spec    x50
   debit_spec   = $debit_spec     x50
   merge_spec   = $merge_spec     x50
   credit_stats = $credit_stats
   credit_iarr  = $credit_iarr
   debit_stats  = $debit_stats
   debit_iarr   = $debit_iarr
   p_in         = $p_in           perp
   p_out        = $p_out          perp
   * @param transactions
   * @deprecatedx
   */
/*  private void getStats(List<Transaction> transactions) {
    long min = 0;
    long max = 0;
    Map<Integer,Integer> idToIn = new HashMap<Integer, Integer>();
    Map<Integer,Integer> idToOut = new HashMap<Integer, Integer>();

    for (Transaction t : transactions) {
      if (t.time < min) min = t.time;
      if (t.time > max) max = t.time;

      Integer out = idToOut.get(t.source);
      idToOut.put(t.source, out == null ? 1 : out+1);

      Integer in  = idToIn.get(t.target);
      idToIn.put(t.target,  in == null  ? 1 : in+1);
    }

    logger.debug("min time " + new Date(min) + " max " + new Date(max));
  }*/

  /**
   * TODO : bitcoin too big to fit into memory... :(
   *
   *
   * @param connection
   * @return
   * @throws Exception
   * @deprecatedx
   */
/*  private List<Transaction> getTransactions(DBConnection connection) throws Exception {
      String sql = "select SOURCE,TARGET,TIME,AMOUNT from TRANSACTIONS where SOURCE <> 25 AND TARGET <> 25";

      PreparedStatement statement = connection.getConnection().prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      List<Transaction> transactions = new ArrayList<Transaction>();
      List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
      int c = 0;

      while (rs.next()) {
        c++;
        if (c % 100000 == 0) logger.debug("read  " +c);
        Transaction e = new Transaction(rs);
        transactions.add(e);
      }

      if (false) {
        logger.debug("Got " + rows.size() + " ");
        for (Map<String, String> row : rows) logger.debug(row);
      }
      rs.close();
      statement.close();
      return  transactions;
  }*/


  /**
   * Store two integers in a long.
   * <p>
   * longs are 64 bits, store the low int in the low 32 bits, and the high int in the upper 32 bits.
   *
   * @param low  this is converted from an integer
   * @param high
   * @return
   */
  public static MyEdge storeTwo(long low, long high) {
//    long combined = low;
//    combined += high << 32;
//    return combined;

    // long l = (high << 32) | (low & 0xffffffffL);
    return new MyEdge(low, high);
  }

  /**
   * @param combined
   * @return
   */
  public static long getLow(MyEdge combined) {
    return combined.source;
  }

  /**
   * @param combined
   * @return
   */
  public static long getHigh(MyEdge combined) {
    return combined.target;
  }

  /**
   * @return
   * @throws Exception
   * @paramx dataFilename
   * @paramx users        transactions must be between the subset of non-trivial users (who have more than 10 transactions)
   * @seex #BitcoinFeatures(DBConnection, String, String, boolean)
   */
/*  protected Map<Integer, UserFeatures> getTransForUsers(String dataFilename, Collection<Integer> users) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFilename), "UTF-8"));
    String line;
    int count = 0;
    long t0 = System.currentTimeMillis();
    int max = Integer.MAX_VALUE;
    int bad = 0;

    Map<Integer, UserFeatures> idToStats = new HashMap<Integer, UserFeatures>();

    while ((line = br.readLine()) != null) {
      count++;
      if (count > max) break;
      String[] split = line.split("\\s+"); // 4534248 25      25      2013-01-27 22:41:38     9.91897304
      if (split.length != 6) {
        bad++;
        if (bad < 10) logger.warn("badly formed line " + line);
      }

      int source = Integer.parseInt(split[1]);
      int target = Integer.parseInt(split[2]);

      boolean onlyGetDailyData = period == PERIOD.DAY;
      Calendar calendar = Calendar.getInstance();
      if (users.contains(source) && users.contains(target)) {
        long time = getTime(split, onlyGetDailyData, calendar);
        double amount = Double.parseDouble(split[5]);
        addTransaction(idToStats, source, target, time, amount);
      }
      if (count % 1000000 == 0) {
        logger.debug("read " + count + " transactions... " + (System.currentTimeMillis() - 1.0 * t0) / count + " ms/read");
      }
    }
    if (bad > 0) logger.warn("Got " + bad + " transactions...");
    return idToStats;
  }*/

  void addTransaction(Map<Long, UserFeatures> idToStats, long source, long target, long time, double amount) {
    UserFeatures sourceStats = idToStats.get(source);
    if (sourceStats == null) idToStats.put(source, sourceStats = new UserFeatures(source));
    UserFeatures targetStats = idToStats.get(target);
    if (targetStats == null) idToStats.put(target, targetStats = new UserFeatures(target));

    Transaction trans = new Transaction(source, target, time, amount);

    sourceStats.addDebit(trans);
    targetStats.addCredit(trans);
  }
/*
  private long getTime(String[] split, boolean onlyGetDailyData, Calendar calendar) {
    long time;
    String yyyyMMdd = split[3];
    if (onlyGetDailyData) {
      getDay(calendar, yyyyMMdd);
    } else {
      getDayAndTime(calendar, yyyyMMdd, split[4]);
    }
    time = calendar.getTimeInMillis();
    return time;
  }

  private void getDay(Calendar calendar, String yyyyMMdd) {
    String year = yyyyMMdd.substring(0, 4);
    String month = yyyyMMdd.substring(5, 7);
    String day = yyyyMMdd.substring(8, 10);
    // logger.debug("value " +year + " " + month + " " + day);
    int imonth = Integer.parseInt(month) - 1;
    calendar.set(Integer.parseInt(year), imonth, Integer.parseInt(day));
  }

  private void getDayAndTime(Calendar calendar, String yyyyMMdd, String hhmmss) {
    String year = yyyyMMdd.substring(0, 4);
    String month = yyyyMMdd.substring(5, 7);
    String day = yyyyMMdd.substring(8, 10);
    // logger.debug("value " +year + " " + month + " " + day);
    int imonth = Integer.parseInt(month) - 1;

  *//*  String hour = hhmmss.substring(0, 2);
    String min = hhmmss.substring(3, 5);
    String sec = hhmmss.substring(6, 8);*//*

    // logger.debug("value " +year + " " + month + " " + day + " " + hour + " " + min + " " + sec);

    calendar.set(Integer.parseInt(year), imonth, Integer.parseInt(day));
  }*/

  /**
   * TODO : fill this in -- note we want only the real components:
   * <p>
   * Julia from Wade:
   * function spectrum(timeLine)
   * data = timeLine["total"].data
   * r = zeros(int(speclen/2) + 1)
   * n = 0.0
   * for i = 1:int(speclen/2):length(data)
   * ds = (i + speclen > length(data)) ? [ data[i:length(data)]; zeros(speclen - (length(data) - i)) ] : data[i:(i+speclen)]
   * r += abs(rfft(ds))
   * n += 1
   * end
   * fdata = r / n
   * fstep = 0.5 / (length(fdata) - 1)
   * <p>
   * tfreq = DataFrame()
   * tfreq["power"]  = fdata[2:end]
   * <p>
   * return tfreq
   * end
   */

  private float[] getSpectrum(float[] signal) {
    return getSpectrum(signal, SPECLEN);
  }

  /**
   * Skip first real component, since just the sum of the inputs
   * <p>
   * TODO : we always have a zero at the end of the results -- seems like we always have n-1 fft values after
   * throwing away DC component.
   *
   * @param signal
   * @param speclen
   * @return
   */
  private float[] getSpectrum(float[] signal, int speclen) {
    FloatFFT_1D fft = new FloatFFT_1D(speclen);
    int half = speclen / 2;
    float[] tmp = new float[speclen];
    float[] r = new float[half];
    float n = 0;

    for (int i = 0; i < signal.length; i += half) {
      int length = speclen;
      if (i + length > signal.length) {
        Arrays.fill(tmp, 0f);
        length = signal.length - i;
        //  logger.debug("at end " + i + " len " + length + " vs " + signal.length);
      }
      System.arraycopy(signal, i, tmp, 0, length);
      //for (int x = 0; x < tmp.length; x++) logger.debug(x + " : " + tmp[x]);
      fft.realForward(tmp);
      // int k = 0;
      for (int j = 1; j < half; j++) {
        int i1 = j * 2;
        //   logger.debug("at " + i1);
        float realComponent = tmp[i1];
        r[j - 1] = Math.abs(realComponent);
      }
      n += 1;
    }
    for (int i = 0; i < r.length; i++) r[i] /= n;

    return r;
  }

  /**
   * Filter out accounts that have less than {@link #MIN_TRANSACTIONS} transactions.
   * NOTE : throws out "supernode" #25
   *
   * @param connection
   * @return
   * @throws Exception
   */
  Collection<Long> getUsers(DBConnection connection) throws Exception {
    long then = System.currentTimeMillis();

	  /*
     * Grab only those users having more than MIN_TRANSACTIONS total transactions
	   * (as either source or target); filter out BITCOIN_OUTLIER
	   */
    /*
    String sql =
        "select source, count(*) as cnt from "+BitcoinBinding.TRANSACTIONS+" "+
            "where source <> " +
            BITCOIN_OUTLIER +
            " " +
            //"group by source having cnt > " +
            //MIN_TRANSACTIONS +
            (LIMIT ? " limit " + USER_LIMIT : "");
	   */

	  /*
	   * Execute updates to figure out
	   */
    String dropTables = "drop table temp if exists;" +

        " drop table temp2 if exists;";
    String sql =
        dropTables +

            " create table temp as select source as uid, count(*) as num_trans " +
            " from " + BitcoinBinding.TRANSACTIONS +
            " where source <> " + BITCOIN_OUTLIER +
            " group by source;" +

            " insert into temp (uid,num_trans)" +
            " select target, count(*) from " + BitcoinBinding.TRANSACTIONS +
            " where target <> " + BITCOIN_OUTLIER +
            " group by target;" +

//            " drop table temp2 if exists;" +
            " create table temp2 as select uid, sum(num_trans) as tot_num_trans" +
            " from temp group by uid having tot_num_trans >= " + MIN_TRANSACTIONS + ";" +
//        " drop table temp;" +
            " select * from temp2;";

    PreparedStatement statement = connection.getConnection().prepareStatement(sql);
    statement.executeUpdate();

	  /*
	   * Execute query to load in active-enough users...
	   */
    String sqlQuery = "select * from temp2;";
    statement = connection.getConnection().prepareStatement(sqlQuery);
    ResultSet rs = statement.executeQuery();

    Set<Long> ids = new HashSet<>();
    int c = 0;

    while (rs.next()) {
      c++;
      if (c % 100000 == 0) logger.debug("read  " + c);
      ids.add(rs.getLong(1));
    }
    long now = System.currentTimeMillis();
    logger.debug("took " + (now - then) + " millis to read " + ids.size() + " users");

    rs.close();
    statement.close();
    return ids;
  }

  private static final int MB = (1024 * 1024);

  public static void logMemory() {
    Runtime rt = Runtime.getRuntime();
    long free = rt.freeMemory();
    long used = rt.totalMemory() - free;
    long max = rt.maxMemory();
    long l = max / MB;
    long l1 = used / MB;

    float fmax = (float) l;
    float fused = (float) l1;

    if (fused / fmax > 0.5) {
      logger.debug("heap info free " + free / MB + "M used " + l1 + "M max " + l + "M");
    }
  }

  public static void rlogMemory() {
    String message = getMemoryStatus();
    Runtime rt = Runtime.getRuntime();
    if (rt.freeMemory() > rt.maxMemory()/2) {
      logger.debug(message);
    }
  }

  public static String getMemoryStatus() {
    Runtime rt = Runtime.getRuntime();
    long free = rt.freeMemory();
    long used = rt.totalMemory() - free;
    long max = rt.maxMemory();
    long l = max / MB;
    long l1 = used / MB;

    return "heap info free " + free / MB + "M used " + l1 + "M max " + l + "M";
  }

/*  public static void main(String[] args) {
    try {
      String dataFilename = null;
      //boolean useSpectralFeatures = false;
      if (args.length > 0) {
        dataFilename = args[0];
        logger.debug("got " + dataFilename);
      }
      //if (args.length > 1) {
      //  useSpectralFeatures = args[1].toLowerCase().contains("spectral");
      //}
      long then = System.currentTimeMillis();

      String database = "bitcoin";
      new BitcoinFeatures(database, ".", dataFilename);

      long now = System.currentTimeMillis();
      logger.debug("took " + (now - then) + " millis to generate features");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }*/
}
