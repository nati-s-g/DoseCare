package com.vaccinetracker.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vaccinetracker.model.*;
import javafx.beans.property.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StorageService {
    
    private static final String DATA_DIR = "data";
    private static final String ADMINS_FILE = DATA_DIR + "/admins.json";
    private static final String PARENTS_FILE = DATA_DIR + "/parents.json";
    private static final String VACCINATORS_FILE = DATA_DIR + "/vaccinators.json";
    private static final String CHILDREN_FILE = DATA_DIR + "/children.json";
    private static final String ALERTS_FILE = DATA_DIR + "/alerts.json";
    private static final String SITES_FILE = DATA_DIR + "/sites.json";
    private static final String VACCINES_FILE = DATA_DIR + "/vaccines.json";
    private static final String RECORDS_FILE = DATA_DIR + "/records.json";
    private static final String STAFF_FILE = DATA_DIR + "/staff.json";
    
    private static Gson gson;
    
    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeHierarchyAdapter(StringProperty.class, new StringPropertyAdapter())
                .registerTypeHierarchyAdapter(IntegerProperty.class, new IntegerPropertyAdapter())
                .registerTypeHierarchyAdapter(BooleanProperty.class, new BooleanPropertyAdapter())
                .registerTypeHierarchyAdapter(DoubleProperty.class, new DoublePropertyAdapter())
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
        List<Vaccinator> vaccinators = new ArrayList<>();
        
        for (User u : allUsers) {
            if (u instanceof Admin) admins.add((Admin) u);
            else if (u instanceof Parent) parents.add((Parent) u);
            else if (u instanceof Vaccinator) vaccinators.add((Vaccinator) u);
        }
        
        saveData(ADMINS_FILE, admins);
        saveData(PARENTS_FILE, parents);
        saveData(VACCINATORS_FILE, vaccinators);
        saveData(CHILDREN_FILE, ChildService.getAllData());
        saveData(ALERTS_FILE, AlertService.getAllData());
        saveData(SITES_FILE, VaccinationSiteService.getAllData());
        saveData(VACCINES_FILE, VaccineService.getAllData());
        saveData(RECORDS_FILE, VaccinationService.getAllData());
        saveData(STAFF_FILE, HumanResourceService.getAllData());
        
        System.out.println("All data saved successfully to " + new File(DATA_DIR).getAbsolutePath());
    }
    
    public static void loadAll() {
        File adminsFile = new File(ADMINS_FILE);
        File parentsFile = new File(PARENTS_FILE);
        
        // Check if main user files exist. If not, assume first run.
        if (!adminsFile.exists() && !parentsFile.exists()) {
            System.out.println("No data files found. Initializing sample data...");
            UserService.initializeSampleUsers();
            ChildService.initializeDummyData();
            VaccineService.preloadVaccines();
            VaccinationSiteService.initializeSampleSites();
            AlertService.initializeSampleAlerts();
            VaccinationService.initializeDummyData();
            HumanResourceService.initializeDummyData();
            
            saveAll();
            return;
        }

        // Load Admins and Parents separately
        List<Admin> admins = loadData(ADMINS_FILE, new TypeToken<List<Admin>>(){}.getType());
        List<Parent> parents = loadData(PARENTS_FILE, new TypeToken<List<Parent>>(){}.getType());
        List<Vaccinator> vaccinators = loadData(VACCINATORS_FILE, new TypeToken<List<Vaccinator>>(){}.getType());
        
        // Ensure default vaccinator exists if file was missing/empty
        if (vaccinators == null) {
            vaccinators = new ArrayList<>();
        }
        
        List<User> allUsers = new ArrayList<>();
        if (admins != null) allUsers.addAll(admins);
        if (parents != null) allUsers.addAll(parents);
        if (vaccinators != null) allUsers.addAll(vaccinators);
        
        // Only update if we found data, otherwise keep the default sample data
        if (!allUsers.isEmpty()) {
            UserService.setAllData(allUsers);
        }
        
        List<Child> children = loadData(CHILDREN_FILE, new TypeToken<List<Child>>(){}.getType());
        if (children != null && !children.isEmpty()) {
            ChildService.setAllData(children);
        } else {
            // Initialize dummy data if file missing or empty
            ChildService.initializeDummyData();
        }
        
        List<HealthAlert> alerts = loadData(ALERTS_FILE, new TypeToken<List<HealthAlert>>(){}.getType());
        if (alerts != null && !alerts.isEmpty()) AlertService.setAllData(alerts);
        
        List<VaccinationSite> sites = loadData(SITES_FILE, new TypeToken<List<VaccinationSite>>(){}.getType());
        if (sites != null && !sites.isEmpty()) {
            VaccinationSiteService.setAllData(sites);
        } else {
            VaccinationSiteService.initializeSampleSites();
        }
        
        List<Vaccine> vaccines = loadData(VACCINES_FILE, new TypeToken<List<Vaccine>>(){}.getType());
        if (vaccines != null && !vaccines.isEmpty()) {
            VaccineService.setAllData(vaccines);
        } else {
            VaccineService.preloadVaccines();
        }
        
        // Ensure new vaccines are added even if data was loaded
        VaccineService.preloadVaccines();
        
        List<VaccinationRecord> records = loadData(RECORDS_FILE, new TypeToken<List<VaccinationRecord>>(){}.getType());
        if (records != null && !records.isEmpty()) VaccinationService.setAllData(records);
        
        // Ensure dummy data exists if missing (e.g. for Vaccinator demo)
        VaccinationService.initializeDummyData();
        
        List<StaffMember> staff = loadData(STAFF_FILE, new TypeToken<List<StaffMember>>(){}.getType());
        if (staff != null && !staff.isEmpty()) {
            HumanResourceService.setAllData(staff);
        } else {
            HumanResourceService.initializeDummyData();
        }
        
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

    private static class StringPropertyAdapter extends TypeAdapter<StringProperty> {
        @Override
        public void write(JsonWriter out, StringProperty value) throws IOException {
            out.value(value == null ? null : value.get());
        }

        @Override
        public StringProperty read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return new SimpleStringProperty(in.nextString());
        }
    }

    private static class IntegerPropertyAdapter extends TypeAdapter<IntegerProperty> {
        @Override
        public void write(JsonWriter out, IntegerProperty value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.get());
            }
        }

        @Override
        public IntegerProperty read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return new SimpleIntegerProperty(in.nextInt());
        }
    }

    private static class BooleanPropertyAdapter extends TypeAdapter<BooleanProperty> {
        @Override
        public void write(JsonWriter out, BooleanProperty value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.get());
            }
        }

        @Override
        public BooleanProperty read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return new SimpleBooleanProperty(in.nextBoolean());
        }
    }

    private static class DoublePropertyAdapter extends TypeAdapter<DoubleProperty> {
        @Override
        public void write(JsonWriter out, DoubleProperty value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.get());
            }
        }

        @Override
        public DoubleProperty read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return new SimpleDoubleProperty(in.nextDouble());
        }
    }
}
