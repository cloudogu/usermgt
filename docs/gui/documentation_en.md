# Documentation

The **User Management** is a Dogu for **managing the users and groups of the Cloudogu EcoSystem**. In addition to your own personal data, you can create, edit and delete users and groups, depending on the rights you have.

The **User Management** can be accessed via the Warp Menu in the "Administration" area.

![Warp Menu with User Management](figures/usermanagement/CESUsermanagement_Warp_en.png)

## Personal data

As a user of the Cloudogu EcoSystem you can change **your personal data** in the account area.

![Image of page header with focus on navbar with logged in user](figures/usermanagement/CESUsermanagement_UserAccount_en.png)

The personal account area is displayed directly when you open the **User Management**. Via the 
form you can adjust your personal data like your e-mail address or your password. The adjustments you make will only be **updated** by clicking on the save button. The username cannot be changed.

In the lower part of the account area you can see your assigned groups.

![Image of assigned groups](figures/usermanagement/CESUsermanagement_AssignedGroups_en.png)

## Administration of accounts

As an administrator you have the possibility to create, delete and edit users in the **User Management**.

### Search for accounts

If you manage a large number of users, the **search function** will help you find the account you need.

1. Select the "users" tab.

![User overview with two users](figures/usermanagement/CESUsermanagement_Users_en.png)

2. In the filter section, enter the username, display name or e-mail address of the account you want to find and press *Enter*. The table will show you the accounts matching your search criterion.

![User overview filtered by user testuser](figures/usermanagement/CESUsermanagement_UsersSearchResult_en.png)

To remove the filter again, click on the "X" symbol inside the search field.

### Changing account data

To change the data of an account, first click on the pencil icon in the row of the account you want to change.

![Change user data](figures/usermanagement/CESUsermanagement_EditUser_en.png)

Afterwards, you can make changes and save them by clicking the "Save" button. 
Besides the account information like email address or display name, you can also change the password. In addition to that, the **User Management** offers the possibility to force the user to change the password at the next login.

Note that you **cannot** change the username.

### Deleting accounts

To delete an account, go to the "Users" page and click on the trash can icon in the row of the account you want to delete. A confirmation prompt follows, which you must confirm before the account is finally deleted.

Note that accounts deleted in **User Management** are not automatically deleted in the Dogus as well.

![Delete account](figures/usermanagement/CESUsermanagement_DeleteUser_en.png)

### Creating new accounts

To create a new account for the Cloudogu EcoSystem, first call up the "Users" tab and click on the "Create user" button.

![User Management](figures/usermanagement/CESUsermanagement_Users_en.png)

A form will open where you can enter the following attributes of the new account:

* Username* (for logging into the Cloudogu EcoSystem).
* First name
* Last name
* Display name (displayed name of the user in the individual Dogus)
* Email* (user will be notified via this email)
* Password (for logging into the Cloudogu EcoSystem)
* "User must change password at next login" (If this option is enabled, the user must change the password at the next login).

\* A user's email address and username are **unique properties** and may therefore only be used for one account. When creating an account, the system validates whether email address and username are unique. If this is not the case, you will receive a meaningful error message and you will be able to edit the account details.

> Note that the username is **unchangeable** after the account has been created.

![Create new user](figures/usermanagement/CESUsermanagement_NewUser_en.png)

You create the account by clicking on the "Save" button.

![User newly created](figures/usermanagement/CESUsermanagement_NewUserCreated_en.png)

After you have saved, the newly created account will be displayed on the "Users" page. 
If you want to make further changes, click on the pencil icon in the last column.

### Password policies

In the Cloudogu EcoSystem configuration, **password policies** can be configured to be validated when passwords are entered. 
By creating reasonable password policies, the security of passwords can be controlled globally.

When you create a password, you will always see the password policies that are not satisfied. Once a password policy is satisfied, it will disappear.

![Not all rules satisfied](figures/usermanagement/CESUsermanagement_Password_Policy_Not_All_Rules_Statisfied_en.png)

You can't save the password until all password policies are satisfied.

If all password policies are fulfilled, the input field will be marked green and the new password can be saved.

![All rules satisfied](figures/usermanagement/CESUsermanagement_Password_Policy_All_Rules_Satisfied_en.png)

## Administration of groups

As an administrator, the **User Management** gives you the ability to **create, edit, or delete groups, as well as manage the members of groups**.

Groups can be used to manage different permission configurations for different permission configuration. More information about this can be found in the section [Permission concept in the Cloudogu EcoSystem](#permission-concept-in-the-cloudogu-ecosystem).

### System groups

In the **User Management** you will find two predefined groups. Members of these groups have special permissions in the Cloudogu EcoSystem Dogus.

**Manager Group**

Members of the **Manager Group** have **full access to the User Management** of the Cloudogu EcoSystem.
This gives users the authorization to create and manage additional users and groups.
Beyond that, no other permissions are associated with the *Manager Group**.

You can change the *Manager group* to be used by changing the entry `/config/_global/manager_group` in the configuration of the Cloudogu EcoSystem to the desired group:

```shell
etcdctl set /config/_global/manager_group newManagerGroup
```

The **User Management** Dogu needs to be restarted to take the change into account.

**Admin Group**

Members of this group have administrative rights in **all** Dogus of the Cloudogu EcoSystem. 
These users can use the administrative functions in the individual Dogus and thus, for example, install plug-ins or make application settings.

The usage of the **Backup & Restore** Dogu is restricted to the administrators.
Consequently, only users who are members of the **Admin group** have access to the **Backup & Restore** Dogu.

You can change the **Admin group** to be used by changing the entry
`/config/_global/admin_group` in the configuration of the Cloudogu EcoSystem to the desired group:

```shell
etcdctl set /config/_global/admin_group newAdminGroup
```

All Dogus needs to be restarted for the change to take effect.

### Creating a new group

To avoid assigning individual permissions for all accounts in the Dogus, you can create groups as described below:

Select the "Groups" tab in **User Management**.

![Header of the group overview](figures/usermanagement/CESUsermanagement_GroupsOverviewHeader_en.png)

Click on the "Create group" button.

![Empty group form](figures/usermanagement/CESUsermanagement_NewGroup_en.png)

Then define the properties of the new group:
  * Name*
  * Description

  \* The name of a group is a **unique property** and therefore may only be used for one group. When creating a group, the system checks if the name is unique. If it is not, you will get a meaningful error message and you will be able to change the name of the group.

> Note that the group name is unchangeable after the group has been created. 

Create the group by clicking on the "Save" button.

### Group assignment

There are two ways to assign an account to a group:
* Via editing the account
* Via editing the group

**Group assignment via editing an account:**.

1. In the "Users" tab, select the pencil icon for the corresponding account.
2. Then enter the group name in the "Groups" area under *Add group*. A list of suggestions will automatically appear according to the input you have made.
3. Click on the desired group to assign the account to the group.

![](figures/usermanagement/CESUsermanagement_AssignGroups_en.png)

4. Click on the "Save" button to save the changes.

**Group assignment via editing a group:**.

1. In the "Groups" tab, select the pencil icon for the corresponding group.
2. In the "Members" area, you can add the user name of the desired member. A list of suggestions will appear automatically according to the input made. 
3. Click on the desired user name in the list of suggestions to add the account to the group. 

![](figures/usermanagement/CESUsermanagement_AssignUsers_en.png)

4. Click on the "Save" button to save the assignment.

### Deleting a group

To delete a group, click on the trash can icon of the respective group in the "Groups" area and confirm the security prompt.

System groups **cannot** be deleted.

Note that groups deleted in the **User Management** are not automatically deleted in the Dogus. However, the assignment of accounts to groups is synchronized.

## Permission concept in the Cloudogu EcoSystem

The permission concept of the Cloudogu EcoSystem is based on a **central user management** and a **decentralized permission configuration**: Accounts and groups can be stored in the **User Management**. These are propagated to the other Dogus of the Cloudogu EcoSystem, allowing you to assign permissions for groups or individual users decentralized in each Dogu.

### User Management

The **User Management** is used to manage accounts and groups. In doing so, the **User Management** uses an internal *LDAP* as directory service.

In addition to using the **User Management** provided by us, you have the option of using an **external directory service** for the Cloudogu EcoSystem. In this case, user management would **not** be done via the Dogu **User Management** presented here, but via the external directory service you have connected.

### Permission concepts of the Dogus

You can create the accounts for users of the Cloudogu EcoSystem centrally in the **User Management**. To simplify the permission configuration, you can create groups for different user purposes. A user can belong to more than one group. A group can have more than one member.

Accounts and groups are **synchronized** with the Dogus, meaning in each Dogu you will find the accounts and groups created in the **User Management**. 

Since Dogus may be applications developed outside of the Cloudogu EcoSystem, the **permission concept in the Dogus may differ** - as you can see in the following diagram exemplary.

![Rights concept in the Dogus](figures/usermanagement/RoleConceptCloudoguEcoSystem_en.png)

For more information about the permission concept of individual Dogus, please refer to the documentation of the respective Dogu.

You can also create accounts and groups directly in the Dogus. Please note that these are then not known to other Dogus and cannot be managed in the **User Management**. Creating accounts or groups outside of the **User Management** is therefore **not** recommended.

### Synchronization of accounts and groups

User accounts and groups are forwarded to a Dogu **as soon as the the respective account logs into the Dogu**. For this purpose, the user's group assignments are queried every time the user logs on to a Dogu via the CAS (Central Authentication Service) - the central *single sign-on* authentication service of the Cloudogu EcoSystem.

As can be seen in the following diagram, the CAS passes the required user information to the Dogu after successful authentication. The Dogu then validates whether the user's account and groups already exist. If this is not the case, the account and groups are created and assigned if the Dogu supports this procedure.

In Dogus that do not manage groups or accounts, no groups or accounts will be created. For other Dogus, the creation of accounts and groups is often done by a CAS plugin that has been designed for this purpose.

 ![Synchronization of accounts and groups](figures/usermanagement/CES_UserManagement_Synchronization_Groups_en.png)

 If a user's account has already been created internally in a Dogu, the internal account will be linked to the external CAS account if the internal user name matches the external user name. This *usually* overwrites the internal data in the Dogu with the data from the **User Management** and the account is marked as an external account in the Dogu, if possible.

When creating accounts and groups in the **User Management**, please note that modified or newly created accounts, groups and group assignments are **not directly** known in other dogus, but **will be made known the next time the user logs in to the respective Dogu**.

There are exceptions for the **Dogus Jira and Confluence**: A synchronization of the user data is triggered every 60 minutes by default; this setting can be reduced in etcd via the key ```ldap/sync_interval```.
Alternatively, a user can log in to CAS and then log in again to Jira / Confluence so that the synchronization for the user is completed in this Dogu.
Another alternative is that the administrative user triggers a synchronization manually via: "Settings (gear)" > "User management" > "User directories" > "CES LDAP mapper dogu" > "Synchronize".
Synchronization by time or manually by the administrative user only needs to be carried out for one of the two dogus, the other dogu is also synchronized.

### Possible procedure for permission configuration

If you as administrator want to create a new group and configure it directly in different Dogus, the procedure shown in the following diagram can be recommended.

 ![Procedure for rights configuration](figures/usermanagement/CES_UserManagement_example_en.png)

With the help of a test account you can log into the Dogus where you want to configure the newly created group. After you have configured the group with your account in the Dogu, you can additionally use the test account to test the configuration.

## User import

### Importing new user accounts

User Management provides the option of importing any number of user accounts in just one step via the interface.
For this purpose, a CSV file must be created that contains all accounts to be imported.

CSV according to [RFC 4180](https://datatracker.ietf.org/doc/html/rfc4180) is used as import format. The header of the file must be the following
**7** defined columns:

```csv
username,displayname,givenname,surname,mail,pwdReset,external
```
The order of the columns can vary, but the names of the columns must be kept. 

The columns describe the following information:
* **username** - username of the account
   * If the specified username already exists, this account will be updated with the specified information and no new account will be created.
   * Cannot be changed after an account has been created
**displayname** - Display name of the account
**givenname** - First name of the user
**surname** - Last name of the user
**mail** - Mail address of the user
* **pwdReset** - Indicates whether the password must be reset at the next login
  * Specified using *false* or *true*, where *true* indicates that the password must be reset at the next login
   * For newly created accounts, *pwdReset* is always automatically set to *true
* **external** - Specifies whether the account has been imported from an external service
   * Specification using *false* or *true*, where *true* indicates that the account comes from an external service
   * The connection to an external service is currently still being implemented, therefore *external* is currently always set to *false

An example of a working CSV file:
```
displayname,external,mail,pwdreset,surname,username,givenname
Max Mustermann,TRUE,max@mustermann.de,TRUE,Mustermann,mmustermann,Max
Maria Musterfrau,TRUE,maria@musterfrau.de,TRUE,Musterfrau,mmusterfrau,Maria
Mark Muster,TRUE,mark@muster.de,TRUE,Muster,mmuster,Mark
Claus,TRUE,claus@c.de,TRUE,Claus,,Claus
Claus,TRUE,max@mustermann.de,TRUE,Claus,Claus,Carl
Mark Muster,TRUE,mark@muster.de,TRUE,Muster,mmuster,Mark
```

To import this file, you need to navigate to the 'User Import' item via the navigation bar in User Management.
The page should then look like this:

![Userimport empty page](figures/usermanagement/UserImportPageEmpty_en.png)

In the file input field that can be seen there, the previously created CSV file must be selected.
If a CSV file was selected, the view of the page changes and the content of the CSV file is displayed in tabular form.

![user import file selected](figures/usermanagement/UserImportFileSelected_en.png)

By clicking on 'Import' the CSV file is sent and the user accounts are imported.

Afterward the result page is displayed. There you can see if the import was successful.
It is possible that new user accounts will be created, existing accounts will be updated or lines from the CSV file will be ignored
because they contains invalid values. This can be the case, for example, if necessary columns were not present
or a condition such as unique mails was not met.

On the results page the results are displayed as follows:

![UserImport Results Page Collapsed](figures/usermanagement/UserImportResultPageCollapsed_en.png)

By clicking on the respective rows, more details about the added / updated user accounts as well as the
skipped lines can be displayed.

![User import result page uncollapsed](figures/usermanagement/UserImportResultPageUncollapsed_en.png)

Also, by clicking on 'Download Import Summary' the result can be downloaded.

### Managing past imports

Past imports are stored in the file system that they can be retrieved again.
The import overview is used for this purpose. Therefore, click in the navigation bar on the entry
'Import overviews'. There, all past imports are displayed in tabular form with the name of the CSV file as well as the exact date.

![Import History](figures/usermanagement/UserImportSummaries_en.png)

By clicking on the selection field 'Functions' of the respective line a small submenu opened. There you can download the respective overview directly, delete it from the system or display it in detail with a click on 'Details'.

![Import History Functions](figures/usermanagement/UserImportSummariesFunctions_en.png)
