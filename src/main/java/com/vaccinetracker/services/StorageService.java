package com.vaccinetracker.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vaccinetracker.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StorageService {
    
    private static final String DATA_DIR = "data";
    private static final String ADMINS_FILE = DATA_DIR + "/admins.json";
    private static final String PARENTS_FILE = DATA_DIR + "/parents.json";
    private static final String CHILDREN_FILE = DATA_DIR + "/children.json";
    private static final String ALERTS_FILE = DATA_DIR + "/alerts.json";
    private static final String SITES_FILE = DATA_DIR + "/sites.json";
    private static final String VACCINES_FILE = DATA_DIR + "/vaccines.json";
    private static final String RECORDS_FILE = DATA_DIR + "/records.json";
    
    private static Gson gson;
    
    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        
        // Create data directory if it doesn't exist
        new File(DATA_DIR).mkdirs();
    }
    
    public static void saveAll() {
        // Split users into Admins and Parents to handle polymorphism
        List<User> allUsers = UserService.getAllData();
        List<Admin> admins = new ArrayList<>();
        List<Parent> parents = new ArrayList<>();
        
        for (User u : allUsers) {
            if (u instanceof Admin) admins.add((Admin) u);
            else if (u instanceof Parent) parents.add((Parent) u);
        }
        
        saveData(ADMINS_FILE, admins);
        saveData(PARENTS_FILE, parents);
        saveData(CHILDREN_FILE, ChildService.getAllData());
        saveData(ALERTS_FILE, AlertService.getAllData());
        saveData(SITES_FILE, VaccinationSiteService.getAllData());
        saveData(VACCINES_FILE, VaccineService.getAllData());
        saveData(RECORDS_FILE, VaccinationService.getAllData());
        
        System.out.println("All data saved successfully to " + new File(DATA_DIR).getAbsolutePath());
    }
    
    public static void loadAll() {
        // Load Admins and Parents separately
        List<Admin> admins = loadData(ADMINS_FILE, new TypeToken<List<Admin>>(){}.getType());
        List<Parent> parents = loadData(PARENTS_FILE, new TypeToken<List<Parent>>(){}.getType());
        
        List<User> allUsers = new ArrayList<>();
        if (admins != null) allUsers.addAll(admins);
        if (parents != null) allUsers.addAll(parents);
        
        // Only update if we found data, otherwise keep the default sample data
        if (!allUsers.isEmpty()) {
            UserService.setAllData(allUsers);
        }
        
        List<Child> children = loadData(CHILDREN_FILE, new TypeToken<List<Child>>(){}.getType());
        if (children != null && !children.isEmpty()) ChildService.setAllData(children);
        
        List<HealthAlert> alerts = loadData(ALERTS_FILE, new TypeToken<List<HealthAlert>>(){}.getType());
        if (alerts != null && !alerts.isEmpty()) AlertService.setAllData(alerts);
        
        List<VaccinationSite> sites = loadData(SITES_FILE, new TypeToken<List<VaccinationSite>>(){}.getType());
        if (sites != null && !sites.isEmpty()) VaccinationSiteService.setAllData(sites);
        
        List<Vaccine> vaccines = loadData(VACCINES_FILE, new TypeToken<List<Vaccine>>(){}.getType());
        if (vaccines != null && !vaccines.isEmpty()) VaccineService.setAllData(vaccines);
        
        List<VaccinationRecord> records = loadData(RECORDS_FILE, new TypeToken<List<VaccinationRecord>>(){}.getType());
        if (records != null && !records.isEmpty()) VaccinationService.setAllData(records);
        
        System.out.println("All data loaded successfully.");
    }
    
    private static <T> void saveData(String filePath, List<T> data) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
        }
    }
    
    private static <T> List<T> loadData(String filePath, Type type) {
        File file = new File(filePath);
        if (!file.exists()) return null;
        
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            return null;
        }
    }
    
    // Simple LocalDate Adapter
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString());
        }
    }
}
