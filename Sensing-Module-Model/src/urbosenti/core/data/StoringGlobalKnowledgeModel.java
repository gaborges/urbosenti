/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import urbosenti.core.device.model.ActionModel;
import urbosenti.core.device.model.AddressAgentType;
import urbosenti.core.device.model.Agent;
import urbosenti.core.device.model.AgentCommunicationLanguage;
import urbosenti.core.device.model.AgentType;
import urbosenti.core.device.model.CommunicativeAct;
import urbosenti.core.device.model.Component;
import urbosenti.core.device.model.DataType;
import urbosenti.core.device.model.Device;
import urbosenti.core.device.model.Direction;
import urbosenti.core.device.model.Entity;
import urbosenti.core.device.model.Implementation;
import urbosenti.core.device.model.Instace;
import urbosenti.core.device.model.InteractionType;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.Interaction;
import urbosenti.core.device.model.Parameter;
import urbosenti.core.device.model.PossibleContent;
import urbosenti.core.device.model.Service;
import urbosenti.core.device.model.ServiceType;
import urbosenti.core.device.model.State;
import urbosenti.core.device.model.TargetOrigin;

/**
 *
 * @author Guilherme
 */
public class StoringGlobalKnowledgeModel {

    private final List<AgentType> agentTypes;
    private final List<ServiceType> serviceTypes;
    private final List<EntityType> entityTypes;
    private final List<DataType> dataTypes;
    private final List<Implementation> implementationTypes;
    private final List<AgentCommunicationLanguage> agentCommunicationLanguages;
    private final List<CommunicativeAct> communicativeActs;
    private final List<InteractionType> interactionTypes;
    private final List<Direction> interactionDirections;
    private final List<TargetOrigin> targetsOrigins;
    private final List<AddressAgentType> agentAddressTypes;
    private final Device device;
    private final boolean showContent;

    public StoringGlobalKnowledgeModel() {
        this.agentTypes = new ArrayList();
        this.serviceTypes = new ArrayList();
        this.entityTypes = new ArrayList();
        this.dataTypes = new ArrayList();
        this.implementationTypes = new ArrayList();
        this.agentCommunicationLanguages = new ArrayList();
        this.interactionTypes = new ArrayList();
        this.interactionDirections = new ArrayList();
        this.targetsOrigins = new ArrayList();
        this.agentAddressTypes = new ArrayList();
        this.device = new Device();
        this.device.setId(1);
        this.device.setComponents(new ArrayList());
        this.device.setServices(new ArrayList());
        this.communicativeActs = new ArrayList();
        this.showContent = false;
    }

    public Device loadingGeneralDefinitions(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        NodeList nList, nList2;
        Element eElement, eElement2;
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        // agentType
        nList = doc.getDocumentElement().getElementsByTagName("agentType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.agentTypes.add(
                    new AgentType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.agentTypes.get(i).getId() + " description: " + this.agentTypes.get(i).getDescription());
            }
        }
        // serviceType
        nList = doc.getDocumentElement().getElementsByTagName("serviceType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.serviceTypes.add(
                    new ServiceType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.serviceTypes.get(i).getId() + " description: " + this.serviceTypes.get(i).getDescription());
            }
        }
        // objectType
        nList = doc.getDocumentElement().getElementsByTagName("entityType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.entityTypes.add(new EntityType(
                    Integer.parseInt(eElement.getAttribute("id")),
                    eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.entityTypes.get(i).getId() + " description: " + this.entityTypes.get(i).getDescription());
            }
        }
        // dataType
        nList = doc.getDocumentElement().getElementsByTagName("dataType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.dataTypes.add(
                    new DataType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            this.dataTypes.get(i).setInitialValue(eElement.getAttribute("initialValue"));
            if (showContent) {
                System.out.println("id: " + this.dataTypes.get(i).getId() + " description: " + this.dataTypes.get(i).getDescription());
            }
        }
        // implementationType
        nList = doc.getDocumentElement().getElementsByTagName("implementationType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.implementationTypes.add(
                    new Implementation(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.implementationTypes.get(i).getId() + " description: " + this.implementationTypes.get(i).getDescription());
            }
        }
        // agentCommunicationLanguage
        nList = doc.getDocumentElement().getElementsByTagName("agentCommunicationLanguage");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.agentCommunicationLanguages.add(
                    new AgentCommunicationLanguage(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getAttribute("description")));
            if (showContent) {
                System.out.println("id: " + this.agentCommunicationLanguages.get(i).getId() + " description: " + this.agentCommunicationLanguages.get(i).getDescription());
            }
            // communicativeAct
            nList2 = nList.item(i).getChildNodes(); // eElement.getElementsByTagName("communicativeAct");
            for (int j = 0; j < nList2.getLength(); j++) {
                if (nList2.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    eElement2 = (Element) nList2.item(j);
                    this.communicativeActs.add(
                            new CommunicativeAct(
                                    Integer.parseInt(eElement2.getAttribute("id")),
                                    eElement2.getTextContent(),
                                    this.agentCommunicationLanguages.get(i)));
                }
            }
            if (showContent) {
                System.out.println("..." + communicativeActs.size());
            }
        }
        // interactionType
        nList = doc.getDocumentElement().getElementsByTagName("interactionType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.interactionTypes.add(
                    new InteractionType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.interactionTypes.get(i).getId() + " description: " + this.interactionTypes.get(i).getDescription());
            }
        }
        // interactionDirection
        nList = doc.getDocumentElement().getElementsByTagName("interactionDirection");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.interactionDirections.add(
                    new Direction(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.interactionDirections.get(i).getId() + " description: " + this.interactionDirections.get(i).getDescription());
            }
        }
        // targetOrigin
        nList = doc.getDocumentElement().getElementsByTagName("targetOrigin");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.targetsOrigins.add(
                    new TargetOrigin(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.targetsOrigins.get(i).getId() + " description: " + this.targetsOrigins.get(i).getDescription());
            }
        }
        // agentAddressType
        nList = doc.getDocumentElement().getElementsByTagName("agentAddressType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.agentAddressTypes.add(
                    new AddressAgentType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.agentAddressTypes.get(i).getId() + " description: " + this.agentAddressTypes.get(i).getDescription());
            }
        }
        // device
        nList = doc.getDocumentElement().getElementsByTagName("device");
        this.device.setDescription(nList.item(0).getAttributes().getNamedItem("description").getTextContent());
        this.device.setDeviceVersion(Double.parseDouble(nList.item(0).getAttributes().getNamedItem("version").getTextContent()));
        nList = doc.getDocumentElement().getElementsByTagName("generalDefinitions");
        this.device.setGeneralDefinitionsVersion(Double.parseDouble(nList.item(0).getAttributes().getNamedItem("version").getTextContent()));
        nList = doc.getDocumentElement().getElementsByTagName("agentModels");
        this.device.setAgentModelVersion(Double.parseDouble(nList.item(0).getAttributes().getNamedItem("version").getTextContent()));
        // service
        nList = doc.getDocumentElement().getElementsByTagName("service");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.device.getServices().add(
                    new Service());
            this.device.getServices().get(i).setDescription(eElement.getAttribute("description"));
            for (ServiceType st : serviceTypes) {
                if (st.getId() == Integer.parseInt(eElement.getAttribute("serviceType"))) {
                    this.device.getServices().get(i).setServiceType(st);
                    break;
                }
            }
            if (this.device.getServices().get(i).getServiceType() == null) {
                throw new Error("Tipo de serviço não especificado para o serviço: " + this.device.getServices().get(i).getDescription() + " uid: " + this.device.getServices().get(i).getServiceUID());
            }
            this.device.getServices().get(i).setAddress(eElement.getElementsByTagName("address").item(0).getTextContent());
            if (eElement.getElementsByTagName("serviceUID").getLength() > 0) {
                this.device.getServices().get(i).setServiceUID(eElement.getElementsByTagName("serviceUID").item(0).getTextContent());
            }
            if (eElement.getElementsByTagName("applicationUID").getLength() > 0) {
                this.device.getServices().get(i).setApplicationUID(eElement.getElementsByTagName("applicationUID").item(0).getTextContent());
            }
            // agent
            if (eElement.getElementsByTagName("agent").getLength() > 0) {
                nList2 = eElement.getElementsByTagName("agent");
                for (int j = 0; j < nList2.getLength(); j++) {
                    eElement2 = (Element) nList2.item(j);
                    this.device.getServices().get(i).setAgent(new Agent());
                    this.device.getServices().get(i).getAgent().setSystemAddress(
                            eElement2.getAttributes().getNamedItem("address").getTextContent());
                    for (AgentType type : agentTypes) {
                        if (type.getId() == Integer.parseInt(eElement2.getAttributes().getNamedItem("type").getTextContent())) {
                            this.device.getServices().get(i).getAgent().setAgentType(type);
                            break;
                        }
                    }
                    for (AddressAgentType type : agentAddressTypes) {
                        if (type.getId() == Integer.parseInt(eElement2.getAttributes().getNamedItem("type").getTextContent())) {
                            this.device.getServices().get(i).getAgent().setAddressType(type);
                            break;
                        }
                    }
                }
            }
            if (showContent) {
                System.out.println("..." + this.device.getServices().get(i).getAddress());
                System.out.println("..." + this.device.getServices().get(i).getAgent().getSystemAddress());
            }
        }
        /* ***************** Salvar tudo no banco ******************** */
        return device;
    }

    public Device loadingDevice(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList nListComponents, nListEntities, nListElements, nListSubElements, nListSubElements2;
        Element eComponent, eEntity, eElement, eSubElement;

        nListComponents = doc.getDocumentElement().getElementsByTagName("component");
        if (showContent) {
            System.out.println("... " + nListComponents.getLength());
        }
        // Components
        for (int i = 0; i < nListComponents.getLength(); i++) {
            eComponent = (Element) nListComponents.item(i);
            Component c = new Component(
                    eComponent.getAttribute("name"),
                    eComponent.getAttribute("urbosenti.core.device.DeviceManager"));
            nListEntities = eComponent.getElementsByTagName("entity");
            if (showContent) {
                System.out.println("Componemt: " + c.getDescription() + " " + c.getReferedClass());
            }
            // Entities
            for (int j = 0; j < nListEntities.getLength(); j++) {
                eEntity = (Element) nListEntities.item(j);
                Entity entity = new Entity(eEntity.getAttribute("description"));
                for (EntityType type : entityTypes) {
                    if (type.getId() == Integer.parseInt(eEntity.getAttribute("type"))) {
                        entity.setEntityType(type);
                        break;
                    }
                }
                if (showContent) {
                    System.out.println("Entity " + j + " " + entity.getDescription());
                }
                // States
                nListElements = eEntity.getElementsByTagName("state");
                if (nListElements.getLength() > 0) {
                    for (int t = 0; t < nListElements.getLength(); t++) {
                        eElement = (Element) nListElements.item(t);
                        if (eElement.hasAttribute("id")) { // Se tem o atributo id então não é um estado individual de uma instância
                            State state = new State();
                            state.setId(Integer.parseInt(eElement.getAttribute("id")));
                            for (DataType type : dataTypes) {
                                if (type.getId() == Integer.parseInt(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("dataType").getTextContent())) {
                                    state.setDataType(type);
                                    break;
                                }
                            }
                            // verificar se possui algum valor opcional
                            if (eElement.hasAttribute("description")) {
                                state.setDescription(eElement.getAttribute("description"));
                            }
                            if (eElement.hasAttribute("userCanChange")) {
                                if (eElement.getAttribute("userCanChange").equals("true")) {
                                    state.setUserCanChange(true);
                                } else {
                                    state.setUserCanChange(false);
                                }
                            }
                            if (eEntity.getElementsByTagName("content").item(0).getAttributes().getNamedItem("initialValue") != null) {
                                state.setInitialValue(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                            }
                            if (eEntity.getElementsByTagName("content").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                                state.setSuperiorLimit(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                            }
                            if (eEntity.getElementsByTagName("content").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                                state.setInferiorLimit(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                            }
                            if (eElement.hasAttribute("instanceState")) {
                                if (eElement.getAttribute("instanceState").equals("true")) {
                                    state.setStateInstance(true);
                                } else {
                                    state.setStateInstance(false);
                                }
                            }
                            // verificar se possui algum valor possível
                            nListSubElements = ((Element) eEntity.getElementsByTagName("content").item(0)).getElementsByTagName("value");
                            if (nListSubElements.getLength() > 0) {
                                for (int z = 0; z < nListSubElements.getLength(); z++) {
                                    eSubElement = (Element) nListSubElements.item(z);
                                    PossibleContent pc = new PossibleContent(eSubElement.getTextContent());
                                    if (eSubElement.hasAttribute("default")) {
                                        if (eSubElement.getAttribute("default").equals("true")) {
                                            state.setStateInstance(true);
                                        } else {
                                            state.setStateInstance(false);
                                        }
                                    }
                                    state.getPossibleContents().add(pc);
                                }
                            }
                            entity.getStates().add(state);
                        }
                    }
                    if (showContent) {
                        System.out.println("State count: " + nListElements.getLength());
                    }
                }
                // Instances
                nListElements = eEntity.getElementsByTagName("instance");
                if (nListElements.getLength() > 0) {
                    // Pega a classe que representa as instâncias
                    String representativeClass = eEntity.getElementsByTagName("instances").item(0).getAttributes().getNamedItem("representativeClass").getTextContent();
                    // Adicionar as instâncias e seus estados
                    for (int z = 0; z < nListElements.getLength(); z++) {
                        eElement = (Element) nListElements.item(z);
                        entity.getInstaces().add(new Instace(
                                Integer.parseInt(eElement.getAttribute("id")),
                                eElement.getAttribute("description"),
                                representativeClass));

                        // Verificar se há estados internos e sobreescrever as configurações individuais
                        nListSubElements = eElement.getElementsByTagName("state");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListSubElements.item(t);
                            for (State s : entity.getStates()) {
                                if (s.getId() == Integer.parseInt(eSubElement.getAttribute("stateId"))) {
                                    // sobreescrever configurações
                                    if (eSubElement.hasAttribute("initialValue")) {
                                        s.setInitialValue(eSubElement.getAttribute("initialValue"));
                                    }
                                    if (eSubElement.hasAttribute("superiorLimit")) {
                                        s.setSuperiorLimit(eSubElement.getAttribute("superiorLimit"));
                                    }
                                    if (eSubElement.hasAttribute("inferiorLimit")) {
                                        s.setInferiorLimit(eSubElement.getAttribute("inferiorLimit"));
                                    }
                                    if (eSubElement.getElementsByTagName("value").getLength() > 0) {
                                        // Se há conteúdos os de s são removidos e acidionados novos
                                        s.setPossibleContent(new ArrayList());
                                        for (int n = 0; n < eSubElement.getElementsByTagName("value").getLength(); n++) {
                                            eSubElement = (Element) nListSubElements.item(z);
                                            PossibleContent pc = new PossibleContent(
                                                    ((Element) eSubElement.getElementsByTagName("value").item(n))
                                                    .getTextContent());
                                            if (((Element) eSubElement.getElementsByTagName("value").item(n)).hasAttribute("default")) {
                                                if (((Element) eSubElement.getElementsByTagName("value").item(n)).getAttribute("default").equals("true")) {
                                                    s.setStateInstance(true);
                                                } else {
                                                    s.setStateInstance(false);
                                                }
                                            }
                                            s.getPossibleContents().add(pc);
                                        }
                                    }
                                    // adicionar
                                    entity.getInstaces().get(z).getStates().add(s);
                                    break;
                                }
                            }
                        }
                    }
                    if (showContent) {
                        System.out.println("Instance count: " + nListElements.getLength());
                    }
                }
                // Events
                nListElements = eEntity.getElementsByTagName("event");
                if (nListElements.getLength() > 0) {
                    for (int z = 0; z < nListElements.getLength(); z++) {
                        eElement = (Element) nListElements.item(z);
                        EventModel event = new EventModel();
                        event.setId(Integer.parseInt(eElement.getAttribute("id")));
                        event.setDescription(eElement.getAttribute("description"));
                        if (eElement.hasAttribute("implementation")) {
                            for (Implementation type : implementationTypes) {
                                if (type.getId() == Integer.parseInt(eElement.getAttribute("implementation"))) {
                                    event.setImplementation(type);
                                    break;
                                }
                            }
                        } else {
                            event.setImplementation(implementationTypes.get(0));
                        }
                        // Target
                        nListSubElements = eElement.getElementsByTagName("target");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListComponents.item(t);
                            event.getTargets().add(new TargetOrigin(eSubElement.getTextContent(),
                                    eSubElement.getAttribute("mandatory").equals("true")));
                        }
                        // Parameter
                        nListSubElements = eElement.getElementsByTagName("parameter");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListSubElements.item(t);
                            event.getParameters().add(new Parameter(eSubElement.getAttribute("label"))); // Label obrigatório
                            if (eSubElement.hasAttribute("dataType")) {
                                for (DataType type : dataTypes) { // data type obrigatório, se não adicionado deve pegar do estado
                                    if (type.getId() == Integer.parseInt(eSubElement.getAttribute("dataType"))) {
                                        event.getParameters().get(t).setDataType(type);
                                    }
                                }
                            } else {
                                if (!eSubElement.hasAttribute("state")) {
                                    throw new Error("Tipo de dado não informado para o parâmetro " + event.getParameters().size() + " da entidade " + entity.getDescription());
                                }
                            }
                            // id - opcional
                            if (eSubElement.hasAttribute("id")) {
                                event.getParameters().get(t).setId(Integer.parseInt(eSubElement.getAttribute("id")));
                            }
                            // optional - opcional
                            if (eSubElement.hasAttribute("optional")) {
                                event.getParameters().get(t).setOptional(eSubElement.getAttribute("optional").equals("true"));
                            }
                            // state - optional
                            if (eSubElement.hasAttribute("state")) {
                                for (State state : entity.getStates()) {
                                    if (state.getId() == Integer.parseInt(eSubElement.getAttribute("state"))) {
                                        //event.getParameters().get(t).setInferiorLimit(new Object());
                                        event.getParameters().get(t).setInferiorLimit(state.getInferiorLimit());
                                        //event.getParameters().get(t).setSuperiorLimit(new Object());
                                        event.getParameters().get(t).setSuperiorLimit(state.getSuperiorLimit());
                                        //event.getParameters().get(t).setInitialValue(new Object());
                                        event.getParameters().get(t).setInitialValue(state.getInitialValue());
                                        event.getParameters().get(t).setPossibleContents(state.getPossibleContents());
                                        if (event.getParameters().get(t).getDataType() == null) {
                                            event.getParameters().get(t).setDataType(state.getDataType());
                                        }
                                    }
                                }
                            }
                            // description - opcional
                            if (eSubElement.getElementsByTagName("description").getLength() > 0) {
                                event.getParameters().get(t).setDescription(eSubElement.getElementsByTagName("description").item(0).getTextContent());
                            }
                            // possibleValues - opcional
                            if (eSubElement.getElementsByTagName("possibleValues").getLength() > 0) {
                                // superiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                                    event.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                                }
                                // inferiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                                    event.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                                }
                                // initialValue
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") != null) {
                                    event.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                                }
                                // value
                                nListSubElements2 = eSubElement.getElementsByTagName("value");
                                if (nListSubElements2.getLength() > 0) {
                                    event.getParameters().get(t).setPossibleContents(new ArrayList<PossibleContent>());
                                    for (int n = 0; n < nListSubElements2.getLength(); n++) {
                                        event.getParameters().get(t).getPossibleContents().add(
                                                new PossibleContent(nListSubElements2.item(n).getTextContent()));
                                        if (nListSubElements2.item(n).getAttributes().getNamedItem("default") != null) {
                                            event.getParameters().get(t).getPossibleContents().get(n).setIsDefault(
                                                    nListSubElements2.item(n).getAttributes().getNamedItem("default").getTextContent().equals("true"));
                                        }
                                    }
                                }
                            }
                        }
                        //System.out.println("      EventModel parameter count: "+event.getParameters().size());
                        // adiciona os evento na entidade do dispositivo
                        entity.getEvents().add(event);
                    }
                    if (showContent) {
                        System.out.println("    Event count: " + nListElements.getLength());
                    }
                    if (showContent) {
                        System.out.println("    Event count: " + entity.getEvents().size());
                    }
                }
                // Actions
                nListElements = eEntity.getElementsByTagName("action");
                if (nListElements.getLength() > 0) {
                    for (int z = 0; z < nListElements.getLength(); z++) {
                        eElement = (Element) nListElements.item(z);
                        ActionModel action = new ActionModel();
                        // id - obrigatório
                        action.setId(Integer.parseInt(eElement.getAttribute("id")));
                        // descrioption - obrigatório
                        action.setDescription(eElement.getAttribute("description"));
                        // Parameter - opcional
                        nListSubElements = eElement.getElementsByTagName("parameter");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListSubElements.item(t);
                            action.getParameters().add(new Parameter(eSubElement.getAttribute("label"))); // Label obrigatório
                            if (eSubElement.hasAttribute("dataType")) {
                                for (DataType type : dataTypes) { // data type obrigatório, se não adicionado deve pegar do estado
                                    if (type.getId() == Integer.parseInt(eSubElement.getAttribute("dataType"))) {
                                        action.getParameters().get(t).setDataType(type);
                                    }
                                }
                            } else {
                                if (!eSubElement.hasAttribute("state")) {
                                    throw new Error("Tipo de dado não especificado para o parâmetro " + action.getParameters().size() + " da entidade " + entity.getDescription());
                                }
                            }
                            // id - opcional
                            if (eSubElement.hasAttribute("id")) {
                                action.getParameters().get(t).setId(Integer.parseInt(eSubElement.getAttribute("id")));
                            }
                            // optional - opcional
                            if (eSubElement.hasAttribute("optional")) {
                                action.getParameters().get(t).setOptional(eSubElement.getAttribute("optional").equals("true"));
                            }
                            // state - optional
                            if (eSubElement.hasAttribute("state")) {
                                for (State state : entity.getStates()) {
                                    if (state.getId() == Integer.parseInt(eSubElement.getAttribute("state"))) {
                                        //event.getParameters().get(t).setInferiorLimit(new Object());
                                        action.getParameters().get(t).setInferiorLimit(state.getInferiorLimit());
                                        //event.getParameters().get(t).setSuperiorLimit(new Object());
                                        action.getParameters().get(t).setSuperiorLimit(state.getSuperiorLimit());
                                        //event.getParameters().get(t).setInitialValue(new Object());
                                        action.getParameters().get(t).setInitialValue(state.getInitialValue());
                                        action.getParameters().get(t).setPossibleContents(state.getPossibleContents());
                                        if (action.getParameters().get(t).getDataType() == null) {
                                            action.getParameters().get(t).setDataType(state.getDataType());
                                        }
                                    }
                                }
                            }
                            // description - opcional
                            if (eSubElement.getElementsByTagName("description").getLength() > 0) {
                                action.getParameters().get(t).setDescription(eSubElement.getElementsByTagName("description").item(0).getTextContent());
                            }
                            // possibleValues - opcional
                            if (eSubElement.getElementsByTagName("possibleValues").getLength() > 0) {
                                // superiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                                    action.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                                }
                                // inferiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                                    action.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                                }
                                // initialValue
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") != null) {
                                    action.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                                }
                                // value
                                nListSubElements2 = eSubElement.getElementsByTagName("value");
                                if (nListSubElements2.getLength() > 0) {
                                    action.getParameters().get(t).setPossibleContents(new ArrayList<PossibleContent>());
                                    for (int n = 0; n < nListSubElements2.getLength(); n++) {
                                        action.getParameters().get(t).getPossibleContents().add(
                                                new PossibleContent(nListSubElements2.item(n).getTextContent()));
                                        if (nListSubElements2.item(n).getAttributes().getNamedItem("default") != null) {
                                            action.getParameters().get(t).getPossibleContents().get(n).setIsDefault(
                                                    nListSubElements2.item(n).getAttributes().getNamedItem("default").getTextContent().equals("true"));
                                        }
                                    }
                                }
                            }
                        }
                        entity.getActions().add(action);
                    }
                    if (showContent) {
                        System.out.println("    Action count: " + nListElements.getLength());
                    }
                    if (showContent) {
                        System.out.println("    Action count: " + entity.getActions().size());
                    }
                }
                c.getEntities().add(entity);
            }
            this.device.getComponents().add(c);
            if (showContent) {
                System.out.println("  Entity count: " + device.getComponents().get(i).getEntities().size());
            }
        }
        if (showContent) {
            System.out.println("Component count: " + device.getComponents().size());
        }
        return device;
    }

    public Device loadingAgentModels(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList nListAgentModel, nListElements, nListSubElements, nListSubElements2;
        Element eAgentModel, eElement, eSubElement;
        AgentCommunicationLanguage baseAgentCommunicationLanguage;
        int baseAgentType = 0;

        nListAgentModel = doc.getDocumentElement().getElementsByTagName("agentModel");
        for (int i = 0; i < nListAgentModel.getLength(); i++) {
            eAgentModel = (Element) nListAgentModel.item(i);
            // Encontra a acl base para todas as iterações
            for (AgentCommunicationLanguage acl : agentCommunicationLanguages) {
                if (acl.getId() == Integer.parseInt(eAgentModel.getAttribute("acl"))) {
                    baseAgentCommunicationLanguage = acl;
                    break;
                }
            }
            // Encontra o tipo de agente relacionado
            for (int j = 0; j < this.agentTypes.size(); j++) {
                if (this.agentTypes.get(j).getId() == Integer.parseInt(eAgentModel.getAttribute("agentType"))) {
                    baseAgentType = j;
                    break;
                }
            }
            // state
            nListElements = eAgentModel.getElementsByTagName("state");
            for (int j = 0; j < nListElements.getLength(); j++) {
                // content
                eElement = (Element) nListElements.item(j);
                if (eElement.hasAttribute("id")) { // Se tem o atributo id então não é um estado individual de uma instância
                    State state = new State();
                    state.setId(Integer.parseInt(eElement.getAttribute("id")));
                    for (DataType type : dataTypes) {
                        if (type.getId() == Integer.parseInt(eElement.getElementsByTagName("content")
                                .item(0).getAttributes().getNamedItem("dataType").getTextContent())) {
                            state.setDataType(type);
                            break;
                        }
                    }
                    // verificar se possui algum valor opcional
                    if (eElement.hasAttribute("description")) {
                        state.setDescription(eElement.getAttribute("description"));
                    }
                    if (eElement.hasAttribute("userCanChange")) {
                        if (eElement.getAttribute("userCanChange").equals("true")) {
                            state.setUserCanChange(true);
                        } else {
                            state.setUserCanChange(false);
                        }
                    }
                    if (eElement.getElementsByTagName("content").item(0).getAttributes().getNamedItem("initialValue") != null) {
                        state.setInitialValue(eElement.getElementsByTagName("content")
                                .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                    }
                    if (eElement.getElementsByTagName("content").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                        state.setSuperiorLimit(eElement.getElementsByTagName("content")
                                .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                    }
                    if (eElement.getElementsByTagName("content").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                        state.setInferiorLimit(eElement.getElementsByTagName("content")
                                .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                    }
                    if (eElement.hasAttribute("instanceState")) {
                        if (eElement.getAttribute("instanceState").equals("true")) {
                            state.setStateInstance(true);
                        } else {
                            state.setStateInstance(false);
                        }
                    }
                    // verificar se possui algum valor possível
                    nListSubElements = ((Element) eElement.getElementsByTagName("content").item(0)).getElementsByTagName("value");
                    if (nListSubElements.getLength() > 0) {
                        for (int z = 0; z < nListSubElements.getLength(); z++) {
                            eSubElement = (Element) nListSubElements.item(z);
                            PossibleContent pc = new PossibleContent(eSubElement.getTextContent());
                            if (eSubElement.hasAttribute("default")) {
                                if (eSubElement.getAttribute("default").equals("true")) {
                                    state.setStateInstance(true);
                                } else {
                                    state.setStateInstance(false);
                                }
                            }
                            state.getPossibleContents().add(pc);
                        }
                    }
                    this.agentTypes.get(baseAgentType).getStates().add(state);
                }
            }
            if (showContent) {
                System.out.println("State count: " + nListElements.getLength());
            }
            // interaction
            nListElements = eAgentModel.getElementsByTagName("interaction");
            for (int j = 0; j < nListElements.getLength(); j++) {
                eElement = (Element) nListElements.item(j);
                Interaction interaction = new Interaction();
                // id - obrigatório
                interaction.setId(Integer.parseInt(eElement.getAttribute("id")));
                // type - obrigatório
                for (InteractionType type : interactionTypes) {
                    if (type.getId() == Integer.parseInt(eElement.getAttribute("type"))) {
                        interaction.setInteractionType(type);
                    }
                }
                // direction - obrigatório
                for (Direction type : interactionDirections) {
                    if (type.getId() == Integer.parseInt(eElement.getAttribute("direction"))) {
                        interaction.setDirection(type);
                    }
                }
                // description - opcional
                if (eElement.hasAttribute("description")) {
                    interaction.setDescription(eElement.getAttribute("description"));
                }
                // primaryInteraction - obbrigatório se typo secundária
                if (eElement.hasAttribute("primaryInteraction")) {
                    boolean flag = true;
                    for (Interaction interact : this.agentTypes.get(baseAgentType).getInteraction()) {
                        if (interact.getId() == Integer.parseInt(eElement.getAttribute("primaryInteraction"))) {
                            interaction = interact;
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        throw new Error("Interação primária id:\"" + eElement.getAttribute("primaryInteraction") + "\" não encontrada!");
                    }
                }
                if (interaction.getInteractionType().getId() == 2 && !eElement.hasAttribute("primaryInteraction")) {
                    throw new Error("Interação primária não especificada na interação " + interaction.getId());
                }
                // communicativeAct
                for (CommunicativeAct act : communicativeActs) {
                    if (act.getId() == Integer.parseInt(eElement.getElementsByTagName("communicativeAct").item(0).getTextContent())) {
                        interaction.setCommunicativeAct(act);
                        break;
                    }
                }
                // Parameter - opcional
                nListSubElements = eElement.getElementsByTagName("parameter");
                for (int t = 0; t < nListSubElements.getLength(); t++) {
                    eSubElement = (Element) nListSubElements.item(t);
                    interaction.getParameters().add(new Parameter(eSubElement.getAttribute("label"))); // Label obrigatório
                    if (eSubElement.hasAttribute("dataType")) {
                        for (DataType type : dataTypes) { // data type obrigatório, se não adicionado deve pegar do estado
                            if (type.getId() == Integer.parseInt(eSubElement.getAttribute("dataType"))) {
                                interaction.getParameters().get(t).setDataType(type);
                            }
                        }
                    } else {
                        if (!eSubElement.hasAttribute("state")) {
                            throw new Error("Tipo de dado não especificado para o parâmetro " + interaction.getParameters().size() + " da entidade " + interaction.getDescription());
                        }
                    }
                    // id - opcional
                    if (eSubElement.hasAttribute("id")) {
                        interaction.getParameters().get(t).setId(Integer.parseInt(eSubElement.getAttribute("id")));
                    }
                    // optional - opcional
                    if (eSubElement.hasAttribute("optional")) {
                        interaction.getParameters().get(t).setOptional(eSubElement.getAttribute("optional").equals("true"));
                    }
                    // state - optional
                    if (eSubElement.hasAttribute("state")) {
                        for (State state : this.agentTypes.get(baseAgentType).getStates()) {
                            if (state.getId() == Integer.parseInt(eSubElement.getAttribute("state"))) {
                                //event.getParameters().get(t).setInferiorLimit(new Object());
                                interaction.getParameters().get(t).setInferiorLimit(state.getInferiorLimit());
                                //event.getParameters().get(t).setSuperiorLimit(new Object());
                                interaction.getParameters().get(t).setSuperiorLimit(state.getSuperiorLimit());
                                //event.getParameters().get(t).setInitialValue(new Object());
                                interaction.getParameters().get(t).setInitialValue(state.getInitialValue());
                                interaction.getParameters().get(t).setPossibleContents(state.getPossibleContents());
                                if (interaction.getParameters().get(t).getDataType() == null) {
                                    interaction.getParameters().get(t).setDataType(state.getDataType());
                                }
                            }
                        }
                    }
                    // description - opcional
                    if (eSubElement.getElementsByTagName("description").getLength() > 0) {
                        interaction.getParameters().get(t).setDescription(eSubElement.getElementsByTagName("description").item(0).getTextContent());
                    }
                    // possibleValues - opcional
                    if (eSubElement.getElementsByTagName("possibleValues").getLength() > 0) {
                        // superiorLimit
                        if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                            interaction.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                    .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                        }
                        // inferiorLimit
                        if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                            interaction.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                    .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                        }
                        // initialValue
                        if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") != null) {
                            interaction.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                    .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                        }
                        // value
                        nListSubElements2 = eSubElement.getElementsByTagName("value");
                        if (nListSubElements2.getLength() > 0) {
                            interaction.getParameters().get(t).setPossibleContents(new ArrayList<PossibleContent>());
                            for (int n = 0; n < nListSubElements2.getLength(); n++) {
                                interaction.getParameters().get(t).getPossibleContents().add(
                                        new PossibleContent(nListSubElements2.item(n).getTextContent()));
                                if (nListSubElements2.item(n).getAttributes().getNamedItem("default") != null) {
                                    interaction.getParameters().get(t).getPossibleContents().get(n).setIsDefault(
                                            nListSubElements2.item(n).getAttributes().getNamedItem("default").getTextContent().equals("true"));
                                }
                            }
                        }
                    }
                }
                // Adiciona interação
                this.agentTypes.get(baseAgentType).getInteraction().add(interaction);
            }
        }
        if (showContent) System.out.println("Interaction count: " + this.agentTypes.get(baseAgentType).getInteraction().size());
        return device;
    }

    public Device validateGeneralConfigurations(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        NodeList nList, nList2;
        Element eElement, eElement2;
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        // agentType
        nList = doc.getDocumentElement().getElementsByTagName("agentType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.agentTypes.add(
                    new AgentType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.agentTypes.get(i).getId() + " description: " + this.agentTypes.get(i).getDescription());
            }
        }
        // serviceType
        nList = doc.getDocumentElement().getElementsByTagName("serviceType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.serviceTypes.add(
                    new ServiceType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.serviceTypes.get(i).getId() + " description: " + this.serviceTypes.get(i).getDescription());
            }
        }
        // objectType
        nList = doc.getDocumentElement().getElementsByTagName("entityType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.entityTypes.add(new EntityType(
                    Integer.parseInt(eElement.getAttribute("id")),
                    eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.entityTypes.get(i).getId() + " description: " + this.entityTypes.get(i).getDescription());
            }
        }
        // dataType
        nList = doc.getDocumentElement().getElementsByTagName("dataType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.dataTypes.add(
                    new DataType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            this.dataTypes.get(i).setInitialValue(eElement.getAttribute("initialValue"));
            if (showContent) {
                System.out.println("id: " + this.dataTypes.get(i).getId() + " description: " + this.dataTypes.get(i).getDescription());
            }
        }
        // implementationType
        nList = doc.getDocumentElement().getElementsByTagName("implementationType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.implementationTypes.add(
                    new Implementation(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.implementationTypes.get(i).getId() + " description: " + this.implementationTypes.get(i).getDescription());
            }
        }
        // agentCommunicationLanguage
        nList = doc.getDocumentElement().getElementsByTagName("agentCommunicationLanguage");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.agentCommunicationLanguages.add(
                    new AgentCommunicationLanguage(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getAttribute("description")));
            if (showContent) {
                System.out.println("id: " + this.agentCommunicationLanguages.get(i).getId() + " description: " + this.agentCommunicationLanguages.get(i).getDescription());
            }
            // communicativeAct
            nList2 = nList.item(i).getChildNodes(); // eElement.getElementsByTagName("communicativeAct");
            for (int j = 0; j < nList2.getLength(); j++) {
                if (nList2.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    eElement2 = (Element) nList2.item(j);
                    this.communicativeActs.add(
                            new CommunicativeAct(
                                    Integer.parseInt(eElement2.getAttribute("id")),
                                    eElement2.getTextContent(),
                                    this.agentCommunicationLanguages.get(i)));
                }
            }
            if (showContent) {
                System.out.println("..." + communicativeActs.size());
            }
        }
        // interactionType
        nList = doc.getDocumentElement().getElementsByTagName("interactionType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.interactionTypes.add(
                    new InteractionType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.interactionTypes.get(i).getId() + " description: " + this.interactionTypes.get(i).getDescription());
            }
        }
        // interactionDirection
        nList = doc.getDocumentElement().getElementsByTagName("interactionDirection");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.interactionDirections.add(
                    new Direction(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.interactionDirections.get(i).getId() + " description: " + this.interactionDirections.get(i).getDescription());
            }
        }
        // targetOrigin
        nList = doc.getDocumentElement().getElementsByTagName("targetOrigin");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.targetsOrigins.add(
                    new TargetOrigin(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.targetsOrigins.get(i).getId() + " description: " + this.targetsOrigins.get(i).getDescription());
            }
        }
        // agentAddressType
        nList = doc.getDocumentElement().getElementsByTagName("agentAddressType");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.agentAddressTypes.add(
                    new AddressAgentType(
                            Integer.parseInt(eElement.getAttribute("id")),
                            eElement.getTextContent()));
            if (showContent) {
                System.out.println("id: " + this.agentAddressTypes.get(i).getId() + " description: " + this.agentAddressTypes.get(i).getDescription());
            }
        }
        // device
        nList = doc.getDocumentElement().getElementsByTagName("device");
        this.device.setDescription(nList.item(0).getAttributes().getNamedItem("description").getTextContent());
        // service
        nList = doc.getDocumentElement().getElementsByTagName("service");
        for (int i = 0; i < nList.getLength(); i++) {
            eElement = (Element) nList.item(i);
            this.device.getServices().add(
                    new Service());
            this.device.getServices().get(i).setDescription(eElement.getAttribute("description"));
            for (ServiceType st : serviceTypes) {
                if (st.getId() == Integer.parseInt(eElement.getAttribute("serviceType"))) {
                    this.device.getServices().get(i).setServiceType(st);
                    break;
                }
            }
            if (this.device.getServices().get(i).getServiceType() == null) {
                throw new Error("Tipo de serviço não especificado para o serviço: " + this.device.getServices().get(i).getDescription() + " uid: " + this.device.getServices().get(i).getServiceUID());
            }
            this.device.getServices().get(i).setAddress(eElement.getElementsByTagName("address").item(0).getTextContent());
            if (eElement.getElementsByTagName("serviceUID").getLength() > 0) {
                this.device.getServices().get(i).setServiceUID(eElement.getElementsByTagName("serviceUID").item(0).getTextContent());
            }
            if (eElement.getElementsByTagName("applicationUID").getLength() > 0) {
                this.device.getServices().get(i).setApplicationUID(eElement.getElementsByTagName("applicationUID").item(0).getTextContent());
            }
            // agent
            if (eElement.getElementsByTagName("agent").getLength() > 0) {
                nList2 = eElement.getElementsByTagName("agent");
                for (int j = 0; j < nList2.getLength(); j++) {
                    eElement2 = (Element) nList2.item(j);
                    this.device.getServices().get(i).setAgent(new Agent());
                    this.device.getServices().get(i).getAgent().setSystemAddress(
                            eElement2.getAttributes().getNamedItem("address").getTextContent());
                    for (AgentType type : agentTypes) {
                        if (type.getId() == Integer.parseInt(eElement2.getAttributes().getNamedItem("type").getTextContent())) {
                            this.device.getServices().get(i).getAgent().setAgentType(type);
                            break;
                        }
                    }
                    for (AddressAgentType type : agentAddressTypes) {
                        if (type.getId() == Integer.parseInt(eElement2.getAttributes().getNamedItem("type").getTextContent())) {
                            this.device.getServices().get(i).getAgent().setAddressType(type);
                            break;
                        }
                    }
                }
            }
            if (showContent) {
                System.out.println("..." + this.device.getServices().get(i).getAddress());
                System.out.println("..." + this.device.getServices().get(i).getAgent().getSystemAddress());
            }
        }
        /* ***************** Agora pode ser salvo no banco de dados ******************** */
        return device;
    }

    public Device validateDeviceModel(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList nListComponents, nListEntities, nListElements, nListSubElements, nListSubElements2;
        Element eComponent, eEntity, eElement, eSubElement;

        nListComponents = doc.getDocumentElement().getElementsByTagName("component");
        if (showContent) {
            System.out.println("... " + nListComponents.getLength());
        }
        // Components
        for (int i = 0; i < nListComponents.getLength(); i++) {
            eComponent = (Element) nListComponents.item(i);
            Component c = new Component(
                    eComponent.getAttribute("name"),
                    eComponent.getAttribute("urbosenti.core.device.DeviceManager"));
            nListEntities = eComponent.getElementsByTagName("entity");
            if (showContent) {
                System.out.println("Componemt: " + c.getDescription() + " " + c.getReferedClass());
            }
            // Entities
            for (int j = 0; j < nListEntities.getLength(); j++) {
                eEntity = (Element) nListEntities.item(j);
                Entity entity = new Entity(eEntity.getAttribute("description"));
                for (EntityType type : entityTypes) {
                    if (type.getId() == Integer.parseInt(eEntity.getAttribute("type"))) {
                        entity.setEntityType(type);
                        break;
                    }
                }
                if (showContent) {
                    System.out.println("Entity " + j + " " + entity.getDescription());
                }
                // States
                nListElements = eEntity.getElementsByTagName("state");
                if (nListElements.getLength() > 0) {
                    for (int t = 0; t < nListElements.getLength(); t++) {
                        eElement = (Element) nListElements.item(t);
                        if (eElement.hasAttribute("id")) { // Se tem o atributo id então não é um estado individual de uma instância
                            State state = new State();
                            state.setId(Integer.parseInt(eElement.getAttribute("id")));
                            for (DataType type : dataTypes) {
                                if (type.getId() == Integer.parseInt(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("dataType").getTextContent())) {
                                    state.setDataType(type);
                                    break;
                                }
                            }
                            // verificar se possui algum valor opcional
                            if (eElement.hasAttribute("description")) {
                                state.setDescription(eElement.getAttribute("description"));
                            }
                            if (eElement.hasAttribute("userCanChange")) {
                                if (eElement.getAttribute("userCanChange").equals("true")) {
                                    state.setUserCanChange(true);
                                } else {
                                    state.setUserCanChange(false);
                                }
                            }
                            if (eEntity.getElementsByTagName("content").item(0).getAttributes().getNamedItem("initialValue") != null) {
                                state.setInitialValue(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                            }
                            if (eEntity.getElementsByTagName("content").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                                state.setSuperiorLimit(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                            }
                            if (eEntity.getElementsByTagName("content").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                                state.setInferiorLimit(eEntity.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                            }
                            if (eElement.hasAttribute("instanceState")) {
                                if (eElement.getAttribute("instanceState").equals("true")) {
                                    state.setStateInstance(true);
                                } else {
                                    state.setStateInstance(false);
                                }
                            }
                            // verificar se possui algum valor possível
                            nListSubElements = ((Element) eEntity.getElementsByTagName("content").item(0)).getElementsByTagName("value");
                            if (nListSubElements.getLength() > 0) {
                                for (int z = 0; z < nListSubElements.getLength(); z++) {
                                    eSubElement = (Element) nListSubElements.item(z);
                                    PossibleContent pc = new PossibleContent(eSubElement.getTextContent());
                                    if (eSubElement.hasAttribute("default")) {
                                        if (eSubElement.getAttribute("default").equals("true")) {
                                            state.setStateInstance(true);
                                        } else {
                                            state.setStateInstance(false);
                                        }
                                    }
                                    state.getPossibleContents().add(pc);
                                }
                            }
                            entity.getStates().add(state);
                        }
                    }
                    if (showContent) {
                        System.out.println("State count: " + nListElements.getLength());
                    }
                }
                // Instances
                nListElements = eEntity.getElementsByTagName("instance");
                if (nListElements.getLength() > 0) {
                    // Pega a classe que representa as instâncias
                    String representativeClass = eEntity.getElementsByTagName("instances").item(0).getAttributes().getNamedItem("representativeClass").getTextContent();
                    // Adicionar as instâncias e seus estados
                    for (int z = 0; z < nListElements.getLength(); z++) {
                        eElement = (Element) nListElements.item(z);
                        entity.getInstaces().add(new Instace(
                                Integer.parseInt(eElement.getAttribute("id")),
                                eElement.getAttribute("description"),
                                representativeClass));

                        // Verificar se há estados internos e sobreescrever as configurações individuais
                        nListSubElements = eElement.getElementsByTagName("state");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListSubElements.item(t);
                            for (State s : entity.getStates()) {
                                if (s.getId() == Integer.parseInt(eSubElement.getAttribute("stateId"))) {
                                    // sobreescrever configurações
                                    if (eSubElement.hasAttribute("initialValue")) {
                                        s.setInitialValue(eSubElement.getAttribute("initialValue"));
                                    }
                                    if (eSubElement.hasAttribute("superiorLimit")) {
                                        s.setSuperiorLimit(eSubElement.getAttribute("superiorLimit"));
                                    }
                                    if (eSubElement.hasAttribute("inferiorLimit")) {
                                        s.setInferiorLimit(eSubElement.getAttribute("inferiorLimit"));
                                    }
                                    if (eSubElement.getElementsByTagName("value").getLength() > 0) {
                                        // Se há conteúdos os de s são removidos e acidionados novos
                                        s.setPossibleContent(new ArrayList());
                                        for (int n = 0; n < eSubElement.getElementsByTagName("value").getLength(); n++) {
                                            eSubElement = (Element) nListSubElements.item(z);
                                            PossibleContent pc = new PossibleContent(
                                                    ((Element) eSubElement.getElementsByTagName("value").item(n))
                                                    .getTextContent());
                                            if (((Element) eSubElement.getElementsByTagName("value").item(n)).hasAttribute("default")) {
                                                if (((Element) eSubElement.getElementsByTagName("value").item(n)).getAttribute("default").equals("true")) {
                                                    s.setStateInstance(true);
                                                } else {
                                                    s.setStateInstance(false);
                                                }
                                            }
                                            s.getPossibleContents().add(pc);
                                        }
                                    }
                                    // adicionar
                                    entity.getInstaces().get(z).getStates().add(s);
                                    break;
                                }
                            }
                        }
                    }
                    if (showContent) {
                        System.out.println("Instance count: " + nListElements.getLength());
                    }
                }
                // Events
                nListElements = eEntity.getElementsByTagName("event");
                if (nListElements.getLength() > 0) {
                    for (int z = 0; z < nListElements.getLength(); z++) {
                        eElement = (Element) nListElements.item(z);
                        EventModel event = new EventModel();
                        event.setId(Integer.parseInt(eElement.getAttribute("id")));
                        event.setDescription(eElement.getAttribute("description"));
                        if (eElement.hasAttribute("implementation")) {
                            for (Implementation type : implementationTypes) {
                                if (type.getId() == Integer.parseInt(eElement.getAttribute("implementation"))) {
                                    event.setImplementation(type);
                                    break;
                                }
                            }
                        } else {
                            event.setImplementation(implementationTypes.get(0));
                        }
                        // Target
                        nListSubElements = eElement.getElementsByTagName("target");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListComponents.item(t);
                            event.getTargets().add(new TargetOrigin(eSubElement.getTextContent(),
                                    eSubElement.getAttribute("mandatory").equals("true")));
                        }
                        // Parameter
                        nListSubElements = eElement.getElementsByTagName("parameter");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListSubElements.item(t);
                            event.getParameters().add(new Parameter(eSubElement.getAttribute("label"))); // Label obrigatório
                            if (eSubElement.hasAttribute("dataType")) {
                                for (DataType type : dataTypes) { // data type obrigatório, se não adicionado deve pegar do estado
                                    if (type.getId() == Integer.parseInt(eSubElement.getAttribute("dataType"))) {
                                        event.getParameters().get(t).setDataType(type);
                                    }
                                }
                            } else {
                                if (!eSubElement.hasAttribute("state")) {
                                    throw new Error("Tipo de dado não informado para o parâmetro " + event.getParameters().size() + " da entidade " + entity.getDescription());
                                }
                            }
                            // id - opcional
                            if (eSubElement.hasAttribute("id")) {
                                event.getParameters().get(t).setId(Integer.parseInt(eSubElement.getAttribute("id")));
                            }
                            // optional - opcional
                            if (eSubElement.hasAttribute("optional")) {
                                event.getParameters().get(t).setOptional(eSubElement.getAttribute("optional").equals("true"));
                            }
                            // state - optional
                            if (eSubElement.hasAttribute("state")) {
                                for (State state : entity.getStates()) {
                                    if (state.getId() == Integer.parseInt(eSubElement.getAttribute("state"))) {
                                        //event.getParameters().get(t).setInferiorLimit(new Object());
                                        event.getParameters().get(t).setInferiorLimit(state.getInferiorLimit());
                                        //event.getParameters().get(t).setSuperiorLimit(new Object());
                                        event.getParameters().get(t).setSuperiorLimit(state.getSuperiorLimit());
                                        //event.getParameters().get(t).setInitialValue(new Object());
                                        event.getParameters().get(t).setInitialValue(state.getInitialValue());
                                        event.getParameters().get(t).setPossibleContents(state.getPossibleContents());
                                        if (event.getParameters().get(t).getDataType() == null) {
                                            event.getParameters().get(t).setDataType(state.getDataType());
                                        }
                                    }
                                }
                            }
                            // description - opcional
                            if (eSubElement.getElementsByTagName("description").getLength() > 0) {
                                event.getParameters().get(t).setDescription(eSubElement.getElementsByTagName("description").item(0).getTextContent());
                            }
                            // possibleValues - opcional
                            if (eSubElement.getElementsByTagName("possibleValues").getLength() > 0) {
                                // superiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                                    event.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                                }
                                // inferiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                                    event.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                                }
                                // initialValue
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") != null) {
                                    event.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                                }
                                // value
                                nListSubElements2 = eSubElement.getElementsByTagName("value");
                                if (nListSubElements2.getLength() > 0) {
                                    event.getParameters().get(t).setPossibleContents(new ArrayList<PossibleContent>());
                                    for (int n = 0; n < nListSubElements2.getLength(); n++) {
                                        event.getParameters().get(t).getPossibleContents().add(
                                                new PossibleContent(nListSubElements2.item(n).getTextContent()));
                                        if (nListSubElements2.item(n).getAttributes().getNamedItem("default") != null) {
                                            event.getParameters().get(t).getPossibleContents().get(n).setIsDefault(
                                                    nListSubElements2.item(n).getAttributes().getNamedItem("default").getTextContent().equals("true"));
                                        }
                                    }
                                }
                            }
                        }
                        //System.out.println("      EventModel parameter count: "+event.getParameters().size());
                        // adiciona os evento na entidade do dispositivo
                        entity.getEvents().add(event);
                    }
                    if (showContent) {
                        System.out.println("    Event count: " + nListElements.getLength());
                    }
                    if (showContent) {
                        System.out.println("    Event count: " + entity.getEvents().size());
                    }
                }
                // Actions
                nListElements = eEntity.getElementsByTagName("action");
                if (nListElements.getLength() > 0) {
                    for (int z = 0; z < nListElements.getLength(); z++) {
                        eElement = (Element) nListElements.item(z);
                        ActionModel action = new ActionModel();
                        // id - obrigatório
                        action.setId(Integer.parseInt(eElement.getAttribute("id")));
                        // descrioption - obrigatório
                        action.setDescription(eElement.getAttribute("description"));
                        // Parameter - opcional
                        nListSubElements = eElement.getElementsByTagName("parameter");
                        for (int t = 0; t < nListSubElements.getLength(); t++) {
                            eSubElement = (Element) nListSubElements.item(t);
                            action.getParameters().add(new Parameter(eSubElement.getAttribute("label"))); // Label obrigatório
                            if (eSubElement.hasAttribute("dataType")) {
                                for (DataType type : dataTypes) { // data type obrigatório, se não adicionado deve pegar do estado
                                    if (type.getId() == Integer.parseInt(eSubElement.getAttribute("dataType"))) {
                                        action.getParameters().get(t).setDataType(type);
                                    }
                                }
                            } else {
                                if (!eSubElement.hasAttribute("state")) {
                                    throw new Error("Tipo de dado não especificado para o parâmetro " + action.getParameters().size() + " da entidade " + entity.getDescription());
                                }
                            }
                            // id - opcional
                            if (eSubElement.hasAttribute("id")) {
                                action.getParameters().get(t).setId(Integer.parseInt(eSubElement.getAttribute("id")));
                            }
                            // optional - opcional
                            if (eSubElement.hasAttribute("optional")) {
                                action.getParameters().get(t).setOptional(eSubElement.getAttribute("optional").equals("true"));
                            }
                            // state - optional
                            if (eSubElement.hasAttribute("state")) {
                                for (State state : entity.getStates()) {
                                    if (state.getId() == Integer.parseInt(eSubElement.getAttribute("state"))) {
                                        //event.getParameters().get(t).setInferiorLimit(new Object());
                                        action.getParameters().get(t).setInferiorLimit(state.getInferiorLimit());
                                        //event.getParameters().get(t).setSuperiorLimit(new Object());
                                        action.getParameters().get(t).setSuperiorLimit(state.getSuperiorLimit());
                                        //event.getParameters().get(t).setInitialValue(new Object());
                                        action.getParameters().get(t).setInitialValue(state.getInitialValue());
                                        action.getParameters().get(t).setPossibleContents(state.getPossibleContents());
                                        if (action.getParameters().get(t).getDataType() == null) {
                                            action.getParameters().get(t).setDataType(state.getDataType());
                                        }
                                    }
                                }
                            }
                            // description - opcional
                            if (eSubElement.getElementsByTagName("description").getLength() > 0) {
                                action.getParameters().get(t).setDescription(eSubElement.getElementsByTagName("description").item(0).getTextContent());
                            }
                            // possibleValues - opcional
                            if (eSubElement.getElementsByTagName("possibleValues").getLength() > 0) {
                                // superiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("superiorLimit") != null) {
                                    action.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("superiorLimit").getTextContent());
                                }
                                // inferiorLimit
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("inferiorLimit") != null) {
                                    action.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("inferiorLimit").getTextContent());
                                }
                                // initialValue
                                if (eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") != null) {
                                    action.getParameters().get(t).setSuperiorLimit(eSubElement.getElementsByTagName("possibleValues")
                                            .item(0).getAttributes().getNamedItem("initialValue").getTextContent());
                                }
                                // value
                                nListSubElements2 = eSubElement.getElementsByTagName("value");
                                if (nListSubElements2.getLength() > 0) {
                                    action.getParameters().get(t).setPossibleContents(new ArrayList<PossibleContent>());
                                    for (int n = 0; n < nListSubElements2.getLength(); n++) {
                                        action.getParameters().get(t).getPossibleContents().add(
                                                new PossibleContent(nListSubElements2.item(n).getTextContent()));
                                        if (nListSubElements2.item(n).getAttributes().getNamedItem("default") != null) {
                                            action.getParameters().get(t).getPossibleContents().get(n).setIsDefault(
                                                    nListSubElements2.item(n).getAttributes().getNamedItem("default").getTextContent().equals("true"));
                                        }
                                    }
                                }
                            }
                        }
                        entity.getActions().add(action);
                    }
                    if (showContent) {
                        System.out.println("    Action count: " + nListElements.getLength());
                    }
                    if (showContent) {
                        System.out.println("    Action count: " + entity.getActions().size());
                    }
                }
                c.getEntities().add(entity);
            }
            this.device.getComponents().add(c);
            if (showContent) {
                System.out.println("  Entity count: " + device.getComponents().get(i).getEntities().size());
            }
        }
        if (showContent) {
            System.out.println("Component count: " + device.getComponents().size());
        }
        return device;
        /* ********* se chegou até aqui então pode ser salvo no banco de dados ********************/
    }

    public boolean validateAgentModel(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void saveGeneralDefinitions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void saveDevice() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void saveAgentModels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
