package com.vaccinetracker.services;

import com.vaccinetracker.model.StaffMember;
import com.vaccinetracker.model.VaccinationSite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class HumanResourceService {
    private static List<StaffMember> staffList = new ArrayList<>();
    private VaccinationSiteService siteService;
    private static Random random = new Random();

    private static final String[] FEMALE_FIRST_NAMES = {
        "Almaz", "Tigist", "Sara", "Hana", "Mahlet", "Kalkidan", "Aster", "Bethel", "Marta", "Rahel",
        "Lydia", "Helen", "Genet", "Meskerem", "Zewditu"
    };

    private static final String[] MALE_FIRST_NAMES = {
        "Abebe", "Kebede", "Dawit", "Yared", "Kirubel", "Eyob", "Nahom", "Solomon", "Girma", "Tesfaye", 
        "Worku", "Daniel", "Yosef", "Abel", "Elias", "Fikru", "Zelalem", "Mulugeta", "Getachew", "Abraham"
    };

    private static final String[] LAST_NAMES = {
        "Tadesse", "Alemu", "Bekele", "Girma", "Assefa", "Worku", "Yosef", "Mengistu", "Demeke", "Berhanu",
        "Kebede", "Tesfaye", "Haile", "Solomon", "Tekle"
    };

    private static final String[] OTHER_ROLES = {
        "Vaccination Site Manager", "Watchman", "Usher", "Health Worker", "Help Desk", "Queue Manager", "Vaccine Fetcher"
    };

    public HumanResourceService(VaccinationSiteService siteService) {
        this.siteService = siteService;
        // Data is now loaded by StorageService
    }

    public static void initializeDummyData() {
        if (!staffList.isEmpty()) return;
        
        // We need sites to assign staff. Since this is static, we might need to fetch sites statically
        List<VaccinationSite> sites = VaccinationSiteService.getAllData();
        if (sites.isEmpty()) return;

        // Add known staff for testing
        VaccinationSite firstSite = sites.get(0);
        addStaff("Nurse Joy", "Nurse", null, "NUR-001", "joy@center.com", firstSite);
        addStaff("Dr. Oak", "Doctor", null, "DOC-001", "oak@center.com", firstSite);

        // Ensure at least one Nurse, Doctor, and Site Manager per site
        for (VaccinationSite site : sites) {
            createStaffForSite("Nurse", null, site);
            createStaffForSite("Doctor", null, site);
            createStaffForSite("Other", "Vaccination Site Manager", site);
        }

        // Generate extra random staff
        for (int i = 0; i < 10; i++) {
            VaccinationSite randomSite = sites.get(random.nextInt(sites.size()));
            createStaffForSite("Nurse", null, randomSite);
        }
        
        for (int i = 0; i < 5; i++) {
            VaccinationSite randomSite = sites.get(random.nextInt(sites.size()));
            createStaffForSite("Other", null, randomSite);
        }
        
        StorageService.saveAll();
    }

    private static void createStaffForSite(String role, String specificJobTitle, VaccinationSite site) {
        String firstName;
        if ("Nurse".equals(role)) {
            // 80% chance of female for nurses
            if (random.nextDouble() < 0.8) {
                firstName = FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
            } else {
                firstName = MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)];
            }
        } else {
            // 50/50 for others
            if (random.nextBoolean()) {
                firstName = FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
            } else {
                firstName = MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)];
            }
        }

        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String name = firstName + " " + lastName;
        
        String professionalId = generateProfessionalId(role);
        String contact = "+2519" + (10000000 + random.nextInt(90000000));
        
        String jobTitle = specificJobTitle;
        if (jobTitle == null) {
            if ("Other".equals(role)) {
                jobTitle = OTHER_ROLES[random.nextInt(OTHER_ROLES.length)];
            } else {
                jobTitle = role; // Nurse or Doctor
            }
        }
        
        addStaff(name, role, jobTitle, professionalId, contact, site);
    }

    public static String generateProfessionalId(String role) {
        if (role.equals("Nurse")) {
            return "NUR-" + (1000 + random.nextInt(9000));
        } else if (role.equals("Doctor")) {
            return "DOC-" + (1000 + random.nextInt(9000));
        } else {
            return "STF-" + (1000 + random.nextInt(9000));
        }
    }

    public static StaffMember addStaff(String name, String role, String jobTitle, String professionalId, String contactInfo, VaccinationSite site) {
        String id = UUID.randomUUID().toString();
        StaffMember staff = new StaffMember(id, name, role, jobTitle, professionalId, contactInfo, site.getSiteId(), site.getName());
        staffList.add(staff);
        
        // Create Vaccinator account for Doctors and Nurses
        if ("Doctor".equals(role) || "Nurse".equals(role)) {
            // Use professionalId as username, default password "12345678"
            UserService.addVaccinator(id, professionalId, "12345678", name, contactInfo, professionalId, site.getSiteId(), site.getLocation());
        }
        
        StorageService.saveAll();
        return staff;
    }

    public static boolean removeStaff(String id) {
        boolean removed = staffList.removeIf(s -> s.getId().equals(id));
        if (removed) {
            UserService.removeUser(id);
            StorageService.saveAll();
        }
        return removed;
    }

    public List<StaffMember> getAllStaff() {
        return new ArrayList<>(staffList);
    }

    public List<StaffMember> getStaffByRole(String role) {
        return staffList.stream()
                .filter(s -> s.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    // Static access for StorageService
    public static List<StaffMember> getAllData() {
        return staffList;
    }

    public static void setAllData(List<StaffMember> data) {
        staffList = data;
    }
}
