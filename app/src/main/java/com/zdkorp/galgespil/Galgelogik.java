package com.zdkorp.galgespil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Galgelogik {
  private ArrayList<String> muligeOrd  = new ArrayList<>();
  private String ordet;
  private ArrayList<String> brugteBogstaver = new ArrayList<>();
  private String synligtOrd;
  private int antalForkerteBogstaver;
  private boolean sidsteBogstavVarKorrekt;
  private boolean spilletErVundet;
  private boolean spilletErTabt;


  public ArrayList<String> getBrugteBogstaver() {
    return brugteBogstaver;
  }

  public String getSynligtOrd() {
    return synligtOrd;
  }

  public String getOrdet() {
    return ordet;
  }

  public int getAntalForkerteBogstaver() {
    return antalForkerteBogstaver;
  }

  public boolean erSidsteBogstavKorrekt() {
    return sidsteBogstavVarKorrekt;
  }

  public boolean erSpilletVundet() {
    return spilletErVundet;
  }

  public boolean erSpilletTabt() {
    return spilletErTabt;
  }

  public boolean erSpilletSlut() {
    return spilletErTabt || spilletErVundet;
  }


  public Galgelogik() {
    muligeOrd.add("bil");
    muligeOrd.add("computer");
    muligeOrd.add("programmering");
    muligeOrd.add("motorvej");
    muligeOrd.add("busrute");
    muligeOrd.add("gangsti");
    muligeOrd.add("skovsnegl");
    muligeOrd.add("solsort");
    nulstil();
  }

  public void nulstil() {
    brugteBogstaver.clear();
    antalForkerteBogstaver = 0;
    spilletErVundet = false;
    spilletErTabt = false;
    ordet = muligeOrd.get(new Random().nextInt(muligeOrd.size()));
    opdaterSynligtOrd();
  }


  public void opdaterSynligtOrd() {
    synligtOrd = "";
    spilletErVundet = true;
    for (int n=0; n<ordet.length(); n++) {
      String bogstav = ordet.substring(n, n+1);
      if (brugteBogstaver.contains(bogstav)) {
        synligtOrd = synligtOrd + bogstav;
      } else {
        synligtOrd = synligtOrd + "*";
        spilletErVundet = false;
      }
    }
  }

  public void gætBogstav(String bogstav) {
    if (bogstav.length()!=1) return;
    System.out.println("Der gættes på bogstavet: "+bogstav);
    if (brugteBogstaver.contains(bogstav)) return;
    if (spilletErVundet || spilletErTabt) return;

    brugteBogstaver.add(bogstav);

    if (ordet.contains(bogstav)) {
      sidsteBogstavVarKorrekt = true;
      System.out.println("Bogstavet var korrekt: "+bogstav);
    }  else {
      // Vi gættede på et bogstav der ikke var i ordet.
      sidsteBogstavVarKorrekt = false;
      System.out.println("Bogstavet var IKKE korrekt: "+bogstav);
      antalForkerteBogstaver = antalForkerteBogstaver + 1;
      if (antalForkerteBogstaver>5) {
        spilletErTabt = true;
      }
    }
    opdaterSynligtOrd();
  }

  public void logStatus() {
    System.out.println("---------- ");
    System.out.println("- ordet (skjult) = "+ordet);
    System.out.println("- synligtOrd = "+synligtOrd);
    System.out.println("- forkerteBogstaver = "+antalForkerteBogstaver);
    System.out.println("- brugeBogstaver = " + brugteBogstaver);
    if (spilletErTabt) System.out.println("- SPILLET ER TABT");
    if (spilletErVundet) System.out.println("- SPILLET ER VUNDET");
    System.out.println("---------- ");
  }


  public static String hentUrl(String url) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
    StringBuilder sb = new StringBuilder();
    String linje = br.readLine();
    while (linje!=null) {
      sb.append(linje + "\n");
      linje = br.readLine();
    }
    return sb.toString();
  }

  public void hentOrdFraDr() throws IOException {
    String data = hentUrl("http://dr.dk");
    System.out.println("data = "+data);

    data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
    System.out.println("data = "+data);
    muligeOrd.clear();
    muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

    System.out.println("muligeOrd = "+muligeOrd);
    nulstil();
  }
    public void loadGame(Context mContext){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        brugteBogstaver = loadArray("brugtebogstaver", mContext);
        System.out.println("antalforkertebogstaver = " + prefs.getInt("antalforkertebogstaver", 0));
        antalForkerteBogstaver = prefs.getInt("antalforkertebogstaver", 0);
        ordet = prefs.getString("ordet", "");

    }

    public boolean saveArray(ArrayList<String> array, String arrayName, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        return editor.commit();
    }

    public ArrayList<String> loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<String>(size);
        if (array.size()>0){
            for(int i=0;i<size;i++)
                array.set(i, prefs.getString(arrayName + "_" + i, null));
        }
        return array;
    }
}
