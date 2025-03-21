# MyVillage

MyVillage is a comprehensive village management system designed to help communities efficiently manage their residents, animals, resources, and administrative tasks. Built with modern technologies, it provides an intuitive interface for village administrators to maintain accurate records and streamline community management processes.

## Features

### Resident Management
- **Resident Registration**: Add and manage resident information
- **Resident Details**: Track personal information, contact details, and demographics
- **Proof of Address**: Generate and verify proof of address documents
- **Resident Search**: Quickly find residents with powerful search functionality
- **Glossary**: Access to terminology and definitions related to resident management

### Animal Management
- Track and manage animals within the village
- Record animal ownership and details
- Monitor animal health and status

### Resource Management
- Manage community resources and assets
- Track resource allocation and usage
- Monitor resource availability

### Administrative Tools
- Administrative dashboard for village managers
- User management and access control
- System configuration and settings

### Additional Features
- **Multi-language Support**: Application supports localization for different languages
- **PDF Generation**: Create official documents like proof of address certificates
- **Verification System**: Verify documents with unique reference numbers and verification codes

## Technical Details

MyVillage is built using:
- **Kotlin**: Modern, concise programming language
- **Jetpack Compose for Desktop**: Declarative UI framework for desktop applications
- **Exposed SQL**: Kotlin SQL framework for database operations
- **Koin**: Dependency injection framework
- **SQLite Database**: Local database storage for village data
- **Apache PDFBox**: For PDF document generation
- **BCrypt**: For secure password hashing

The application follows an MVVM (Model-View-ViewModel) architecture pattern with clear separation of concerns:
- **Models**: Data structures representing village entities
- **ViewModels**: Business logic and state management
- **UI**: Compose-based user interface components

## System Requirements

- Java 11 or higher
- 4GB RAM minimum (8GB recommended)
- 500MB of disk space
- Windows, macOS, or Linux operating system

## Installation

### Option 1: Download Pre-built Package

1. Download the latest release package for your operating system:
   - Windows: `MyVillage-1.0.0.msi`
   - macOS: `MyVillage-1.0.0.dmg`
   - Linux: `MyVillage-1.0.0.deb`

2. Install the package using your system's standard installation procedure.

### Option 2: Build from Source

1. Ensure you have JDK 11+ and Gradle installed
2. Clone the repository:
   ```
   git clone https://github.com/yourusername/MyVillage.git
   cd MyVillage
   ```

3. Build the application:
   ```
   ./gradlew build
   ```

4. Run the application:
   ```
   ./gradlew run
   ```

5. (Optional) Create a distribution package:
   ```
   ./gradlew packageDmg    # For macOS
   ./gradlew packageMsi    # For Windows
   ./gradlew packageDeb    # For Linux
   ```

## Getting Started

1. Launch the application
2. On first startup, use the default admin credentials:
   - Username: `admin`
   - Password: `admin`
   - **Important**: Change these credentials after first login for security reasons
3. Navigate through the main dashboard to access different modules:
   - Resident Management
   - Animal Management
   - Resource Management
   - Administration
   - Settings
4. Use the intuitive interface to manage village data and generate necessary documents

## User Roles

The application supports different user roles:
- **Administrators**: Full access to all features and administrative functions
- **Standard Users**: Access to day-to-day operations for managing residents, animals, and resources

## Data Security

MyVillage implements several security features:
- Document verification with unique codes
- Hashing for document integrity
- Secure storage of sensitive information
- Password hashing using BCrypt
- Role-based access control

## Database

MyVillage uses SQLite databases for data storage:
- `village.db`: Main database for resident, animal, and resource data
- `audit.db`: Audit log database for tracking system activities

The databases are automatically initialized on first startup.

## Troubleshooting

### Common Issues

1. **Application won't start**
   - Ensure Java 11+ is installed and properly configured
   - Check system logs for error messages

2. **Database errors**
   - If database corruption occurs, backup your data and reinstall the application
   - Contact support for database recovery assistance

3. **PDF generation issues**
   - Ensure you have sufficient disk space
   - Check that the application has write permissions to the output directory

## Contributing

We welcome contributions to MyVillage! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For support or inquiries, please contact us at support@myvillage.com
