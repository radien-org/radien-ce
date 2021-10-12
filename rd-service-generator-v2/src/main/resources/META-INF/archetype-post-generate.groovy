/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.commons.io.FileUtils
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Global Variables
 */
String LINE_SEPARATOR = System.lineSeparator();

String system_property = "system.ms.endpoint.${entityResourceName.toLowerCase()}management=" +
        "http://localhost:8080/${entityResourceName.toLowerCase()}managementservice/v1"

String pretty_config_ms = "<!-- ${entityResourceName.toLowerCase()} service -->\n" +
        "    <url-mapping id=\"${entityResourceName.toLowerCase()}s\">\n" +
        "        <pattern value=\"/module/${entityResourceName.toLowerCase()}s\" />\n" +
        "        <view-id value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}s.oaf\" />\n" +
        "    </url-mapping>\n" +
        "\n" +
        "    <url-mapping id=\"${entityResourceName.toLowerCase()}Create\">\n" +
        "        <pattern value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}Create\" />\n" +
        "        <view-id value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}Create.oaf\" />\n" +
        "    </url-mapping>\n" +
        "\n" +
        "    <url-mapping id=\"${entityResourceName.toLowerCase()}Edit\">\n" +
        "        <pattern value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}Edit\" />\n" +
        "        <view-id value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}Edit.oaf\" />\n" +
        "    </url-mapping>\n" +
        "\n" +
        "    <url-mapping id=\"${entityResourceName.toLowerCase()}Delete\">\n" +
        "        <pattern value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}Delete\" />\n" +
        "        <view-id value=\"/module/${entityResourceName.toLowerCase()}s/${entityResourceName.toLowerCase()}Delete.oaf\" />\n" +
        "    </url-mapping>\n" +
        "</pretty-config>"

String language_properties_ms = LINE_SEPARATOR +
        "####################################################" +
        LINE_SEPARATOR +
        "# ${entityResourceName}ManagementService\n" +
        "####################################################" + LINE_SEPARATOR +
        "rd_${entityResourceName.toLowerCase()}_page=${entityResourceName}\n" +
        "rd_not_exists_records_${entityResourceName.toLowerCase()}s=Not exists records pertaining to ${entityResourceName}s!\n" +
        "rd_${entityResourceName.toLowerCase()}_name_is_mandatory=${entityResourceName} entity name mandatory!\n" +
        "\n" +
        "rd_${entityResourceName.toLowerCase()}s=${entityResourceName}s\n" +
        "rd_add_${entityResourceName.toLowerCase()}=Add\n" +
        "rd_add_${entityResourceName.toLowerCase()}_info=Add new entity ${entityResourceName}\n" +
        "rd_create_${entityResourceName.toLowerCase()}_entity=Create ${entityResourceName}\n" +
        "rd_edit_${entityResourceName.toLowerCase()}=Edit\n" +
        "rd_update_${entityResourceName.toLowerCase()}_entity=Update ${entityResourceName}\n" +
        "rd_delete_${entityResourceName.toLowerCase()}=Delete\n" +
        "rd_delete_${entityResourceName.toLowerCase()}_entity=Delete ${entityResourceName}\n" +
        "\n" +
        "rd_save_${entityResourceName.toLowerCase()}=Save ${entityResourceName} entity info!\n" +
        "rd_delete_${entityResourceName.toLowerCase()}_record_info=Are you confirm to delete the selected " +
        "${entityResourceName.toLowerCase()} entity?\n" +
        "\n" +
        "rd_refresh_${entityResourceName.toLowerCase()}s=Refresh\n" +
        "rd_refresh_${entityResourceName.toLowerCase()}s_info=Refresh ${entityResourceName} Table\n" +
        "rd_return_${entityResourceName.toLowerCase()}s=Return to ${entityResourceName}s Page\n" +
        "rd_${entityResourceName.toLowerCase()}s_actions=${entityResourceName} Actions\n" +
        "\n" +
        "rd_select_${entityResourceName.toLowerCase()}_record_info=Select a record from the ${entityResourceName}s to perform actions?" +
        LINE_SEPARATOR

String client_dependency_ms = "            <dependency>" + LINE_SEPARATOR +
        "                <groupId>io.radien</groupId>" + LINE_SEPARATOR +
        "                <artifactId>${artifactId}-client</artifactId>"  + LINE_SEPARATOR +
        "                <version>\${project.parent.version}</version>" +  LINE_SEPARATOR +
        "            </dependency>"

String oaf_property = "    SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT(" +
        "\"system.ms.endpoint.${entityResourceName.toLowerCase()}management\"),"

String tpl_oaf_ms = "                        <b:navCommandLink value=\"#{msg.rd_${entityResourceName.toLowerCase()}_page}\" rendered=\"#{userSession.active}\" action=\"#{navigation.navigate}\" actionListener=\"#{navigation.navigationAction}\">\n" +
        "                            <f:param name=\"navigationNode\" value=\"${entityResourceName.toLowerCase()}s\" />\n" +
        "                            <f:attribute name=\"navigationNode\" value=\"${entityResourceName.toLowerCase()}s\" />\n" +
        "                        </b:navCommandLink>"

/**
 * Path variables
 */
Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
Path ms_module_temp_path = projectPath.resolve("${artifactId}-temp/")
Path ms_module_pom_xml = projectPath.resolve("pom.xml")

Path ms_module_temp_api_model_path = projectPath.resolve("${artifactId}-temp/src/main/java/api/model")
Path CD_RADIEN_API_MODEL_PATH = projectPath.resolveSibling("rd-api/src/main/java/io/radien/api/model")

Path ms_module_temp_api_service_path = projectPath.resolve("${artifactId}-temp/src/main/java/api/service")
Path CD_RADIEN_API_SERVICE_PATH = projectPath.resolveSibling("rd-api/src/main/java/io/radien/api/service")

Path ms_module_temp_api_exception_path = projectPath.resolve("${artifactId}-temp/src/main/java/api/exception")
Path CD_RADIEN_API_EXCEPTION_PATH = projectPath.resolveSibling("rd-api/src/main/java/io/radien/exception")

Path ms_module_temp_web_webapp_path = projectPath.resolve("${artifactId}-temp/src/main/java/web/webapp")
Path CD_RADIEN_WEB_APP_MODEL_PATH = projectPath.resolveSibling("rd-web/src/main/webapp/module")

Path ms_module_temp_web_impl_webapp_path = projectPath.resolve("${artifactId}-temp/src/main/java/webimpl/webapp")
Path CD_RADIEN_WEB_IMPL_APP_MODEL_PATH = projectPath.resolveSibling("rd-web-impl/src/main/java/io/radien/webapp")


String CD_RADIEN_KERNEL_SYSTEM_PROPERTIES = "./rd-kernel/src/main/resources/system.properties"
String CD_RADIEN_WEB_PRETTY_CONFIG = "./rd-web/src/main/webapp/WEB-INF/pretty-config.xml"
String CD_RADIEN_WEB_LANGUAGE_PROPERTY = "./rd-web/src/main/resources/io/radien/i18n/Language.properties"
String CD_RADIEN_API_OAF_PROPERTY = "./rd-api/src/main/java/io/radien/api/OAFProperties.java"
String CD_RADIEN_WEB_TPL_OAF = "./rd-web/src/main/webapp/WEB-INF/templates/tpl_oaf.xhtml"

String CD_RADIEN_POM_XML = "./pom.xml"
String CD_RADIEN_WEB_POM_XML = "./rd-web/pom.xml"
String CD_RADIEN_WEB_IMPL_POM_XML = "./rd-web-impl/pom.xml"

/**
 * system.properties
 */
append_system_properties(CD_RADIEN_KERNEL_SYSTEM_PROPERTIES, system_property, LINE_SEPARATOR)
/**
 * pretty_config
 */
append_pretty_config(CD_RADIEN_WEB_PRETTY_CONFIG, pretty_config_ms, LINE_SEPARATOR)
/**
 * language_properties
 */
append_language_properties(CD_RADIEN_WEB_LANGUAGE_PROPERTY, language_properties_ms, LINE_SEPARATOR)
/**
 * OAFProperties.java
 */
append_oaf_property(CD_RADIEN_API_OAF_PROPERTY, oaf_property)

/**
 * tpl_oaf.xhtml
 */
append_web_tpl_oaf(CD_RADIEN_WEB_TPL_OAF, tpl_oaf_ms)

/**
 * Invoke method to append client dependency
 */
append_client_dependency_pom_xml(CD_RADIEN_POM_XML, client_dependency_ms, "parent")
append_client_dependency_pom_xml(CD_RADIEN_WEB_POM_XML, client_dependency_ms, "rd-web")
append_client_dependency_pom_xml(CD_RADIEN_WEB_IMPL_POM_XML, client_dependency_ms, "rd-web-impl")

/**
 * Implements subpackages of
 * rd-api module; model, service & exception
 */
move_folder_source_target(ms_module_temp_api_model_path, CD_RADIEN_API_MODEL_PATH)
move_folder_source_target(ms_module_temp_api_service_path, CD_RADIEN_API_SERVICE_PATH)
move_folder_source_target(ms_module_temp_api_exception_path, CD_RADIEN_API_EXCEPTION_PATH)

/**
 * Implements subpackages of
 * rd-web module; webapp/module
 */
move_folder_source_target(ms_module_temp_web_webapp_path, CD_RADIEN_WEB_APP_MODEL_PATH)

/**
 * Implements subpackages of
 * rd-web-impl module; webapp/module
 */
move_folder_source_target(ms_module_temp_web_impl_webapp_path, CD_RADIEN_WEB_IMPL_APP_MODEL_PATH)

/**
 * Deletes microservice temporary module
 */
delete_ms_temp_module(ms_module_temp_path, ms_module_pom_xml)

/**
 * Refactor directories from source to target
 * @param source path folder to be moved
 * @param target path folder to be appended
 * @return refactor directories if success else throws IOException
 */
def move_folder_source_target(source, target){
    println "\n" + "Moving folder from the " + "\n" + "${source} to " + "\n" + " ${target}"
    File srcDir = new File(source as String);
    File destDir = new File(target as String);

    try {
        FileUtils.copyDirectory(srcDir, destDir);
    } catch (IOException e) {
        println "Exception Moving folder from the ${source} to targer ${target} ${e.getMessage()}"
    }
    println "\n" + "Moved folder from the " + "\n" + "${source} to " + "\n" + " ${target}"
}


/**
 * Updates System.properties file
 * @param CD_RADIEN_KERNEL_SYSTEM_PROPERTIES path of system_property
 * @param system_property file to be updated
 * @param LINE_SEPARATOR empty line
 * @return @return if success pretty_config_ms to be appended else FileNotFoundException
 */
def append_system_properties(CD_RADIEN_KERNEL_SYSTEM_PROPERTIES, system_property, LINE_SEPARATOR) {
    try{
        File file = new File(CD_RADIEN_KERNEL_SYSTEM_PROPERTIES)
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))
        bufferedWriter.append(LINE_SEPARATOR);
        bufferedWriter.append(system_property)
        bufferedWriter.close()
    } catch(FileNotFoundException e){
        println "system.properties not exists ${e.getMessage()}"
    }
}

/**
 * This method invokes when a new custom service creates, adds URL's
 * into /radien-development/rd-web/src/main/webapp/WEB-INF/pretty-config.xml
 * @param CD_RADIEN_WEB_PRETTY_CONFIG path to be set
 * @param pretty_config_ms string to be appended
 * @return if success pretty_config_ms to be appended else FileNotFoundException
 */
def append_pretty_config(CD_RADIEN_WEB_PRETTY_CONFIG, pretty_config_ms, LINE_SEPARATOR){
    try{
        File file = new File(CD_RADIEN_WEB_PRETTY_CONFIG)
        List<String> lines = new ArrayList<String>();

        // read the file into lines
        BufferedReader r = new BufferedReader(new FileReader(file));
        String text;
        while ((text = r.readLine()) != null)
            lines.add(text);
        r.close();

        int count = lines.size()-1

        //Append content
        for(int i=count; i>=0; i--){
            String closed_pretty_config_tag = lines.get(i);
            if (closed_pretty_config_tag.matches("</pretty-config>")) {
                lines.remove(i);
                lines.add(LINE_SEPARATOR);
                lines.add(pretty_config_ms);
            }
            if(closed_pretty_config_tag == ""){
                lines.remove(i);
            }
        }

        // write it back
        PrintWriter w = new PrintWriter(new FileWriter(file));
        for (String line : lines)
            w.println(line);
        w.close();
    } catch(FileNotFoundException e){
        println "pretty_config file not exists ${e.getMessage()}"
    }
}

/**
 * Updates Language.properties file
 * @param CD_RADIEN_WEB_LANGUAGE_PROPERTY of path
 * @param language_properties_ms file to be updated
 * @param LINE_SEPARATOR empty line to be append
 * @return if success language_properties to be appended else FileNotFoundException
 */
def append_language_properties(CD_RADIEN_WEB_LANGUAGE_PROPERTY, language_properties_ms, LINE_SEPARATOR){
    try{
        File file = new File(CD_RADIEN_WEB_LANGUAGE_PROPERTY)
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))
        bufferedWriter.append(LINE_SEPARATOR);
        bufferedWriter.append(language_properties_ms)
        bufferedWriter.close()
    } catch(FileNotFoundException e){
        println "Language.properties file not found ${e.getMessage()}"
    }
}

/**
 *
 * @param CD_RADIEN_API_OAF_PROPERTY
 * @param oaf_property
 * @param LINE_SEPARATOR
 * @return
 */
def append_oaf_property(CD_RADIEN_API_OAF_PROPERTY, oaf_property){
    try{
        File file = new File(CD_RADIEN_API_OAF_PROPERTY)
        List<String> lines = new ArrayList<String>();

        // read the file into lines
        BufferedReader r = new BufferedReader(new FileReader(file));
        String text;
        while ((text = r.readLine()) != null)
            lines.add(text);
        r.close();

        int count = lines.size()-1

        //Append content
        for(int i=0; i<count; i++){
            String class_oaf_property = lines.get(i);
            if (class_oaf_property.contains("public enum OAFProperties")) {
                lines.add(i+2, oaf_property);
                break;
            }
        }

        // write it back
        PrintWriter w = new PrintWriter(new FileWriter(file));
        for (String line : lines)
            w.println(line);
        w.close();
    } catch(FileNotFoundException e){
        println "OAFProperties.java file not found ${e.getMessage()}"
    }
}

/**
 * This method appends custom service dependency into the parent pom.xml, rd-web pom.xml and
 * rd-web-impl pom.xml
 * @param CD_POM_XML path of pom.xml file to be append
 * @param client_dependency_ms dependency to be set
 * @param which_pom_xml info
 * @return if success client dependency to be appended else FileNotFoundException
 */
def append_client_dependency_pom_xml(CD_POM_XML, client_dependency_ms, which_pom_xml){
    try{
        File file = new File(CD_POM_XML)
        List<String> lines = new ArrayList<String>();

        // read the file into lines
        BufferedReader r = new BufferedReader(new FileReader(file));
        String text;
        while ((text = r.readLine()) != null)
            lines.add(text);
        r.close();

        int count = lines.size()-1

        //Append content
        for(int i=0; i<count; i++){
            String parent_pom_xml = lines.get(i);
            if (parent_pom_xml.contains("<dependencies>")) {
                lines.add(i+1, client_dependency_ms);
                break;
            }
        }

        // write it back
        PrintWriter w = new PrintWriter(new FileWriter(file));
        for (String line : lines)
            w.println(line);
        w.close();
    } catch(FileNotFoundException e){
        println "Radien ${which_pom_xml} pom.xml file not found ${e.getMessage()}"
    }
}

/**
 *
 * @param CD_RADIEN_WEB_TPL_OAF
 * @param tpl_oaf_ms
 * @param LINE_SEPARATOR
 * @return
 */
def append_web_tpl_oaf(CD_RADIEN_WEB_TPL_OAF, tpl_oaf_ms){
    try{
        File file = new File(CD_RADIEN_WEB_TPL_OAF)
        List<String> lines = new ArrayList<String>();

        // read the file into lines
        BufferedReader r = new BufferedReader(new FileReader(file));
        String text;
        while ((text = r.readLine()) != null)
            lines.add(text);
        r.close();

        int count = lines.size()-1

        //Append content
        for(int i=0; i<count; i++){
            String parent_pom_xml = lines.get(i);
            if (parent_pom_xml.contains("</b:navCommandLink>")) {
                lines.add(i+1, tpl_oaf_ms);
                break;
            }
        }

        // write it back
        PrintWriter w = new PrintWriter(new FileWriter(file));
        for (String line : lines)
            w.println(line);
        w.close();
    } catch(FileNotFoundException e){
        println "Parent pom.xml file not found ${e.getMessage()}"
    }
}

/**
 * Deletes a temporary microservice module
 * @param ms_module_temp_path custom microservice
 * temporary module path
 * @return true if folder deletes successfully
 */
def delete_ms_temp_module(ms_module_temp_path,ms_module_pom_xml){
    println "\n" + "Deleting the ${artifactId}-temp module"
    File folder = new File(ms_module_temp_path as String)
    FileUtils.cleanDirectory(folder)
    folder.deleteDir()
    println "\n" + "Deleted the ${artifactId}-temp module"

    try{
        File file = new File(ms_module_pom_xml as String)
        List<String> lines = new ArrayList<String>();

        // read the file into lines
        BufferedReader r = new BufferedReader(new FileReader(file));
        String text;
        while ((text = r.readLine()) != null)
            lines.add(text);
        r.close();

        int count = lines.size()-1

        //Append content
        for(int i=count; i>=0; i--){
            String closed_pretty_config_tag = lines.get(i);
            if (closed_pretty_config_tag.contains("<module>${artifactId}-temp</module>")) {
                lines.remove(i);
            }
        }

        // write it back
        PrintWriter w = new PrintWriter(new FileWriter(file));
        for (String line : lines)
            w.println(line);
        w.close();
    } catch(FileNotFoundException e){
        println "pom.xml file not exists ${e.getMessage()}"
    }
}

