/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import urbosenti.core.device.model.Instance;
import urbosenti.core.device.model.InteractionType;
import urbosenti.core.device.model.EntityType;
import urbosenti.core.device.model.EventModel;
import urbosenti.core.device.model.EventTarget;
import urbosenti.core.device.model.InteractionModel;
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
    private DataManager dataManager;
    private boolean isSaved;

    private StoringGlobalKnowledgeModel() {
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
        this.isSaved = false;
    }

    public StoringGlobalKnowledgeModel(DataManager dataManager) {
        this();
        this.dataManager = dataManager;
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
                    this.device.getServices().get(i).getAgent().setRelativeAddress(
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
                    for (TargetOrigin type : targetsOrigins) {
                        if (type.getId() == Integer.parseInt(eElement2.getAttributes().getNamedItem("layer").getTextContent())) {
                            this.device.getServices().get(i).getAgent().setLayer(type.getId());
                            break;
                        }
                    }
                }
            }
            if (showContent) {
                System.out.println("..." + this.device.getServices().get(i).getAddress());
                System.out.println("..." + this.device.getServices().get(i).getAgent().getAddress());
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
                    eComponent.getAttribute("class"));
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
                                if (type.getId() == Integer.parseInt(eElement.getElementsByTagName("content")
                                        .item(0).getAttributes().getNamedItem("dataType").getTextContent())) {
                                    state.setDataType(type);
                                    state.setInitialValue(type.getInitialValue());
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
                                            pc.setIsDefault(true);
                                            // Se o valor inicial não foi adicionado, o valor default passa a ser o inicial
                                            if (eElement.getElementsByTagName("content").item(0).getAttributes().getNamedItem("initialValue") == null) {
                                                state.setInitialValue(eSubElement.getTextContent());
                                            }
                                        } else {
                                            pc.setIsDefault(false);
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
                        entity.getInstaces().add(new Instance(
                                Integer.parseInt(eElement.getAttribute("id")),
                                eElement.getAttribute("description"),
                                representativeClass));

                        // Verificar se há estados internos e sobreescrever as configurações individuais
                        nListSubElements = eElement.getElementsByTagName("state");
                        // percorre estados da entidade para buscar estados de instância
                        for (State s : entity.getStates()) {
                            // se o estado for um estado de instância, então adicionar na instância o estado
                            if (s.isStateInstance()) {
                                // percorre os estados sobreescritos no conhecimento sobrescrevendo as novas configurações
                                for (int t = 0; t < nListSubElements.getLength(); t++) {
                                    eSubElement = (Element) nListSubElements.item(t);
                                    // checa se o estado atual possui o modelId indicado no conhecimento para sobrescrita
                                    if (s.isStateInstance() && s.getId() == Integer.parseInt(eSubElement.getAttribute("stateId"))) {
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
                                                        pc.setIsDefault(true);
                                                        // Se o valor inicial não foi adicionado, o valor default passa a ser o inicial
                                                        if (!eSubElement.hasAttribute("initialValue")) {
                                                            s.setInitialValue(((Element) eSubElement.getElementsByTagName("value").item(n)).getTextContent());
                                                        }
                                                    } else {
                                                        pc.setIsDefault(false);
                                                    }
                                                }
                                                s.getPossibleContents().add(pc);
                                            }
                                        }
                                    }
                                    break;
                                }
                                // adiciona o estado, tanto sobrescrito como não sobrescrito
                                entity.getInstaces().get(z).getStates().add(s);
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
                            eSubElement = (Element) nListSubElements.item(t);
                            EventTarget target = new EventTarget();
                            target.setEvent(event);
                            target.setMandatory(eSubElement.getAttribute("mandatory").equals("true"));
                            for (TargetOrigin to : targetsOrigins) {
                                if (to.getId() == Integer.parseInt(eSubElement.getTextContent())) {
                                    target.setTarget(to);
                                    break;
                                }
                            }
                            event.getTargets().add(target);
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
                                        event.getParameters().get(t).setRelatedState(state);
                                        break;
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
                                            if (event.getParameters().get(t).getPossibleContents().get(n).isIsDefault()
                                                    && eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") == null) {
                                                event.getParameters().get(t).setInitialValue(nListSubElements2.item(n).getTextContent());
                                            }
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
                                        action.getParameters().get(t).setRelatedState(state);
                                        break;
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
                                            if (action.getParameters().get(t).getPossibleContents().get(n).isIsDefault()
                                                    && eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") == null) {
                                                action.getParameters().get(t).setInitialValue(nListSubElements2.item(n).getTextContent());
                                            }
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
                entity.setModelId(c.getEntities().size() + 1);
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
                            state.setInitialValue(type.getInitialValue());
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
                                    pc.setIsDefault(true);
                                    if (eElement.getElementsByTagName("content").item(0).getAttributes().getNamedItem("initialValue") == null) {
                                        state.setInitialValue(eSubElement.getTextContent());
                                    }
                                } else {
                                    pc.setIsDefault(false);
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
                InteractionModel interaction = new InteractionModel();
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
                    for (InteractionModel interact : this.agentTypes.get(baseAgentType).getInteraction()) {
                        if (interact.getId() == Integer.parseInt(eElement.getAttribute("primaryInteraction"))) {
                            interaction.setPrimaryInteraction(interact);
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
                                break;
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
                                interaction.getParameters().get(t).setRelatedState(state);
                                break;
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
                                    if (interaction.getParameters().get(t).getPossibleContents().get(n).isIsDefault()
                                            && eSubElement.getElementsByTagName("possibleValues").item(0).getAttributes().getNamedItem("initialValue") == null) {
                                        interaction.getParameters().get(t).setInitialValue(nListSubElements2.item(n).getTextContent());
                                    }
                                }
                            }
                        }
                    }
                }
                // Adiciona interação
                this.agentTypes.get(baseAgentType).getInteraction().add(interaction);
            }
        }
        if (showContent) {
            System.out.println("Interaction count: " + this.agentTypes.get(baseAgentType).getInteraction().size());
        }
        return device;
    }

    /**
     * Atualmente incompleto.
     *
     * @param file
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Device validateGeneralConfigurations(File file) throws ParserConfigurationException, IOException, SAXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        /* ***************** Agora pode ser salvo no banco de dados ******************** */
    }

    public Device validateDeviceModel(File file) throws ParserConfigurationException, IOException, SAXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        /* ********* se chegou até aqui então pode ser salvo no banco de dados ********************/
    }

    public boolean validateAgentModel(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // generalDefinitions
    public void saveGeneralDefinitions(Connection connection) {
        // função para checar se é necessário atualizar a base

        try {
            Device dev = this.dataManager.getDeviceDAO().getDevice();
            if (dev != null) { // se alguma versão dos dados for atualizada, deleta a base e instala novamente os dados
                if (dev.getAgentModelVersion() < this.device.getAgentModelVersion()
                        || dev.getGeneralDefinitionsVersion() < this.device.getGeneralDefinitionsVersion()
                        || dev.getDeviceVersion() < this.device.getDeviceVersion()) {
                    dropDatabase(connection);
                    createDataBase(connection);
                    this.isSaved = false;
                } else {
                    this.isSaved = true;
                    return;
                }
            } else {
                this.isSaved = false;
            }
            // device
            this.dataManager.getDeviceDAO().insert(device);
            // agentTypes
            for (AgentType type : agentTypes) {
                this.dataManager.getAgentTypeDAO().insert(type);
            }
            // serviceTypes
            for (ServiceType serviceType : serviceTypes) {
                this.dataManager.getServiceTypeDAO().insert(serviceType);
            }
            // entityTypes
            for (EntityType type : entityTypes) {
                this.dataManager.getEntityTypeDAO().insert(type);
            }
            // dataTypes
            for (DataType type : dataTypes) {
                this.dataManager.getDataTypeDAO().insert(type);
            }
            // implementationTypes
            for (Implementation type : implementationTypes) {
                this.dataManager.getImplementationTypeDAO().insert(type);
            }
            // agentCommunicationLanguage
            for (AgentCommunicationLanguage type : agentCommunicationLanguages) {
                this.dataManager.getAgentCommunicationLanguageDAO().insert(type);
            }
            // communicativeAct
            for (CommunicativeAct type : communicativeActs) {
                this.dataManager.getCommunicativeActDAO().insert(type);
            }
            // interactionType
            for (InteractionType type : interactionTypes) {
                this.dataManager.getInteractionTypeDAO().insert(type);
            }
            // interactionDirection
            for (Direction type : interactionDirections) {
                this.dataManager.getInteractionDirectionDAO().insert(type);
            }
            // targetOrigin
            for (TargetOrigin type : targetsOrigins) {
                this.dataManager.getTargetOriginDAO().insert(type);
            }
            // agentAddressType
            for (AddressAgentType type : agentAddressTypes) {
                this.dataManager.getAgentAddressTypeDAO().insert(type);
            }
            // service
            for (Service type : device.getServices()) {
                type.setDevice(device);
                this.dataManager.getServiceDAO().insert(type);
                // agent
                type.getAgent().setService(type);
                this.dataManager.getAgentDAO().insert(type.getAgent());
            }

        } catch (Exception ex) {
            try {
                Logger.getLogger(StoringGlobalKnowledgeModel.class.getName()).log(Level.SEVERE, null, ex);
                // se der um erro ou excessão dar um drop em todas as tabelas
                dropDatabase(connection);
            } catch (SQLException ex1) {
                Logger.getLogger(StoringGlobalKnowledgeModel.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public void saveDevice(Connection connection) {
        try {
            // Se já foi salvo não salvar
            if (this.isSaved) {
                return;
            }
            /// components
            for (Component component : this.device.getComponents()) {
                component.setDevice(this.device);
                this.dataManager.getComponentDAO().insert(component);
                //// entities
                for (Entity entity : component.getEntities()) {
                    entity.setComponent(component);
                    this.dataManager.getEntityDAO().insert(entity);
                    ///// states
                    for (State state : entity.getStates()) {
                        state.setEntity(entity);
                        this.dataManager.getEntityStateDAO().insert(state);
                        if ((state.getPossibleContents() == null) ? false : state.getPossibleContents().size() > 0) {
                            // Possible contents
                            this.dataManager.getEntityStateDAO().insertPossibleContents(state);
                        }
                    }
                    ///// event
                    for (EventModel event : entity.getEvents()) {
                        event.setEntity(entity);
                        this.dataManager.getEventModelDAO().insert(event);
                        // parameters
                        this.dataManager.getEventModelDAO().insertParameters(event);
                        // targets
                        this.dataManager.getEventModelDAO().insertTargets(event);
                        // parameters -> possible contents
                        for (Parameter parameter : event.getParameters()) {
                            // Possible contents
                            this.dataManager.getEventModelDAO().insertPossibleParameterContents(parameter);
                        }

                    }
                    ///// action
                    for (ActionModel action : entity.getActions()) {
                        action.setEntity(entity);
                        this.dataManager.getActionModelDAO().insert(action);
                        // feedbackanswers
                        this.dataManager.getActionModelDAO().insertFeedbackAnswers(action);
                        // parameters
                        this.dataManager.getActionModelDAO().insertParameters(action);
                        // parameters -> possible contents
                        for (Parameter parameter : action.getParameters()) {
                            // Possible contents
                            this.dataManager.getActionModelDAO().insertPossibleParameterContents(parameter);
                        }
                    }
                    ///// instance
                    for (Instance instance : entity.getInstaces()) {
                        instance.setEntity(entity);
                        this.dataManager.getInstanceDAO().insert(instance);
                        // states
                        for (State state : instance.getStates()) {
                            this.dataManager.getInstanceDAO().insertState(state, instance);
                            // states possible contents
                            if ((state.getPossibleContents() != null) ? state.getPossibleContents().size() > 0 : false) {
                                // Possible contents
                                this.dataManager.getInstanceDAO().insertPossibleStateContents(state, instance);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(StoringGlobalKnowledgeModel.class.getName()).log(Level.SEVERE, null, ex);
            try {
                // se der um erro ou excessão dar um drop em todas as tabelas
                dropDatabase(connection);
            } catch (SQLException ex1) {
                Logger.getLogger(StoringGlobalKnowledgeModel.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public void saveAgentModels(Connection connection) {
        try {
            // Se já foi salvo não salvar
            if (this.isSaved) {
                return;
            }
            // agent
            for (AgentType agentModel : agentTypes) {
                /// agent states
                for (State state : agentModel.getStates()) {
                    state.setAgentType(agentModel);
                    this.dataManager.getAgentTypeDAO().insertState(state);
                    // states possible contents
                    if ((state.getPossibleContents() != null) ? state.getPossibleContents().size() > 0 : false) {
                        // Possible contents
                        this.dataManager.getAgentTypeDAO().insertPossibleStateContents(state);
                    }
                }
                /// interaction
                for (InteractionModel interaction : agentModel.getInteraction()) {
                    interaction.setAgentType(agentModel);
                    this.dataManager.getAgentTypeDAO().insertInteraction(interaction);
                    //// parameter
                    this.dataManager.getAgentTypeDAO().insertParameters(interaction);
                    // parameters -> possible contents
                    for (Parameter parameter : interaction.getParameters()) {
                        // Possible contents
                        this.dataManager.getAgentTypeDAO().insertPossibleParameterContents(parameter);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(StoringGlobalKnowledgeModel.class.getName()).log(Level.SEVERE, null, ex);
            try {
                // se der um erro ou excessão dar um drop em todas as tabelas
                dropDatabase(connection);
            } catch (SQLException ex1) {
                Logger.getLogger(StoringGlobalKnowledgeModel.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    void createDataBase(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS devices (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	generalDefinitionsVersion double not null default 0.0,\n"
                + "	deviceVersion double not null default 0.0,\n"
                + "	agentModelVersion double not null default 0.0\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS agent_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS service_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS entity_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS data_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	initial_value varchar(20) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS implementation_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS agent_communication_languages (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS communicative_acts (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	agent_communication_language_id integer not null,\n"
                + "	foreign key (agent_communication_language_id) references agent_communication_languages (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS interaction_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS interaction_directions (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS targets_origins (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS agent_address_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS agents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	address varchar (100) not null default '/',\n"
                + "	layer integer not null default 1,\n"
                + "	agent_type_id integer not null,\n"
                + "	service_id integer not null,\n"
                + "	foreign key (agent_type_id) references agent_types (id),\n"
                + "	foreign key (layer) references targets_origins (id),\n"
                + "	foreign key (service_id) references services (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS services (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	service_uid varchar (200) not null,\n"
                + "	application_uid varchar (200) not null default \"\",\n"
                + "	address varchar (200) not null,\n"
                + "	service_type_id integer not null,\n"
                + "	device_id integer not null,\n"
                + "	foreign key (service_type_id) references service_types (id),\n"
                + "	foreign key (device_id) references devices (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS components (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	code_class varchar (100) not null,\n"
                + "	device_id integer not null,\n"
                + "	foreign key (device_id) references devices (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS entities (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null,\n"
                + "	entity_type_id integer not null,\n"
                + "	component_id integer not null,\n"
                + "	foreign key (component_id) references components (id),\n"
                + "	foreign key (entity_type_id) references entity_types (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS instances (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	model_id integer detault null,\n"
                + "	representative_class varchar(100) not null,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (entity_id) references entities (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS instance_states (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null, \n"
                + "	user_can_change boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	data_type_id integer not null,\n"
                + "	instance_id integer not null,\n"
                + "	state_model_id integer not null,\n"
                + "	foreign key (instance_id) references instances (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS possible_instance_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null, \n"
                + "	default_value boolean not null default false,\n"
                + "	instance_state_id integer not null,\n"
                + "	foreign key (instance_state_id) references instance_states (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS instance_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	monitored_user_instance_id integer default null, \n"
                + "	instance_state_id integer not null,\n"
                + "	foreign key (monitored_user_instance_id) references instances (id), \n"
                + "	foreign key (instance_state_id) references instance_states (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS entity_states (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null, \n"
                + "	user_can_change boolean not null default false,\n"
                + "	instance_state boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	data_type_id integer not null,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (entity_id) references entities (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS possible_entity_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	entity_state_id integer not null,\n"
                + "	foreign key (entity_state_id) references entity_states (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS entity_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	monitored_user_instance_id integer default null,  /* -- para os estados dos normais, o cara que está sendo monitorado */\n"
                + "	entity_state_id integer not null,\n"
                + "	foreign key (monitored_user_instance_id) references instances (id), \n"
                + "	foreign key (entity_state_id) references entity_states (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS events (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null, \n"
                + "	synchronous boolean not null default false,\n"
                + "     store boolean not null default false, "
                + "	implementation_type_id integer not null,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (implementation_type_id) references implementation_types (id), \n"
                + "	foreign key (entity_id) references entities (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS event_targets_origins (\n"
                + "	event_id integer not null,\n"
                + "	target_origin_id integer not null,\n"
                + "	mandatory boolean not null default true,\n"
                + "	primary key (event_id, target_origin_id),\n"
                + "	foreign key (event_id) references events (id),\n"
                + "	foreign key (target_origin_id) references targets_origins (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS event_parameters (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null, \n"
                + "	optional boolean not null default false,\n"
                + "	parameter_label varchar (100) not null,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	entity_state_id integer default null,\n"
                + "	data_type_id integer not null,\n"
                + "	event_id integer not null,\n"
                + "	foreign key (event_id) references events (id),\n"
                + "	foreign key (data_type_id) references data_types (id),\n"
                + "	foreign key (entity_state_id) references entity_states (id)	\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS possible_event_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	event_parameter_id integer not null,\n"
                + "	foreign key (event_parameter_id) references event_parameters (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS event_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	event_parameter_id integer not null,\n"
                + "     generated_event_id integer not null DEFAULT 0, \n"
                + "     foreign key (generated_event_id) references generated_events (id),"
                + "	foreign key (event_parameter_id) references event_parameters (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS actions (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null, \n"
                + "	has_feedback boolean not null default false,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (entity_id) references entities (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS action_parameters (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null, \n"
                + "	label varchar(100) not null,\n"
                + "	optional boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	entity_state_id integer default null,\n"
                + "	data_type_id integer not null,\n"
                + "	action_id integer not null,\n"
                + "	foreign key (action_id) references actions (id),\n"
                + "	foreign key (data_type_id) references data_types (id),\n"
                + "	foreign key (entity_state_id) references entity_states (id)	\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS possible_action_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	action_parameter_id integer not null,\n"
                + "	foreign key (action_parameter_id) references action_parameters (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS action_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	score double precision not null default 0.0,\n"
                + "	action_parameter_id integer not null,\n"
                + "	generated_action_id integer not null DEFAULT 0,\n"
                + "	foreign key (action_parameter_id) references action_parameters (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS action_feedback_answer (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null,\n"
                + "	action_id integer not null,\n"
                + "	foreign key (action_id) references actions (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS interactions (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	agent_type_id integer not null,\n"
                + "	communicative_act_id integer not null,\n"
                + "	interaction_type_id integer not null,\n"
                + "	direction_id integer not null,\n"
                + "	interaction_id integer default null,\n"
                + "	foreign key (agent_type_id) references agent_types (id),\n"
                + "	foreign key (communicative_act_id) references communicative_acts (id),\n"
                + "	foreign key (interaction_type_id) references interaction_types (id),\n"
                + "	foreign key (direction_id) references interaction_directions (id),\n"
                + "	foreign key (interaction_id) references interactions (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS agent_states (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	data_type_id integer not null,\n"
                + "	agent_type_id integer not null,\n"
                + "	foreign key (agent_type_id) references agent_types (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS possible_agent_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	agent_state_id integer not null,\n"
                + "	foreign key (agent_state_id) references agent_states (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS agent_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	agent_state_id integer not null,\n"
                + "	foreign key (agent_state_id) references agent_states (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS interaction_parameters (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null,\n"
                + "	label varchar(100) not null,\n"
                + "	optional boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	agent_state_id integer default null,\n"
                + "	data_type_id integer not null,\n"
                + "	interaction_id integer not null,\n"
                + "	foreign key (agent_state_id) references agent_states (id),\n"
                + "	foreign key (interaction_id) references interactions (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS possible_interaction_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	interaction_parameter_id integer not null,\n"
                + "	foreign key (interaction_parameter_id) references interaction_parameters (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS conversations (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	created_time varchar(100) not null ,\n"
                + "	agent_id integer not null,\n"
                + "	finished_time varchar(100) default null,\n"
                + "	foreign key (agent_id) references agents (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	message_time varchar(100) not null ,\n"
                + "	interaction_id integer not null,\n"
                + "	conversation_id integer not null,\n"
                + "	foreign key (interaction_id) references interactions (id),\n"
                + "	foreign key (conversation_id) references conversations (id)\n"
                + ");\n"
                + "\n"
                + "CREATE TABLE IF NOT EXISTS interaction_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	message_id integer not null,\n"
                + "	interaction_parameter_id integer not null,\n"
                + "     generated_event_id integer not null default 0,"
                + "	foreign key (interaction_parameter_id) references interaction_parameters (id),\n"
                + "	foreign key (message_id) references messages (id)\n"
                + ");\n"
                + "CREATE TABLE IF NOT EXISTS reports (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "     subject integer not null,\n"
                + "	content_type varchar(100) not null,\n"
                + "     priority integer not null,\n"
                + "	content text not null ,\n"
                + "	anonymous_upload boolean not null,\n"
                + "	created_time varchar(100) not null,\n"
                + "	uses_urbosenti_xml_envelope boolean not null,\n"
                + "     content_size integer,\n"
                + "	target_uid varchar(100) not null,\n"
                + "	target_layer integer not null,\n"
                + "	target_address varchar(100),\n"
                + "	origin_uid varchar(100) not null,\n"
                + "	origin_layer integer not null,\n"
                + "	origin_address varchar(100),\n"
                + "	checked boolean not null,\n"
                + "	sent boolean not null,\n"
                + "	timeout integer,\n"
                + "	service_id integer not null,\n"
                + "	foreign key (service_id) references services (id)\n"
                + ");"
                + " CREATE TABLE IF NOT EXISTS generated_events (\n"
                + "   id integer not null primary key autoincrement,\n"
                + "   event_id integer not null,\n"
                + "   entity_id integer,\n"
                + "   component_id integer,\n"
                + "   time varchar(100) not null,\n"
                + "   timeout integer not null,\n"
                + "   event_type integer not null,"
                + "   foreign key (event_id) references events (id)\n"
                + ");\n"
                + " CREATE TABLE IF NOT EXISTS generated_actions (\n"
                + "   id integer not null primary key autoincrement,\n"
                + "   action_model_id integer not null,\n"
                + "   entity_id integer not null,\n"
                + "   component_id integer not null,\n"
                + "   action_type integer not null,\n"
                + "   parameters text,\n"
                + "   response_time varchar (100),\n"
                + "   feedback_id integer,\n"
                + "   feedback_description text,\n"
                + "   execution_plan_id int,\n"
                + "   event_id int not null,\n"
                + "   event_type int not null,\n"
                + "   foreign key (action_model_id) references actions (id),\n"
                + "   foreign key (event_id) references generated_events (id)\n"
                + ");";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void dropDatabase(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql
                = "DROP TABLE agent_address_types;\n"
                + "DROP TABLE communicative_acts;\n"
                + "DROP TABLE agent_communication_languages;\n"
                + "DROP TABLE agent_types;\n"
                + "DROP TABLE data_types;\n"
                + "DROP TABLE implementation_types;\n"
                + "DROP TABLE devices;\n"
                + "DROP TABLE targets_origins;\n"
                + "DROP TABLE agents;\n"
                + "DROP TABLE services;\n"
                + "DROP TABLE interaction_directions;\n"
                + "DROP TABLE interaction_types;\n"
                + "DROP TABLE entity_types;\n"
                + "DROP TABLE service_types;\n"
                + "DROP TABLE \"main\".\"action_contents\";\n"
                + "DROP TABLE \"main\".\"action_feedback_answer\";\n"
                + "DROP TABLE \"main\".\"action_parameters\";\n"
                + "DROP TABLE \"main\".\"actions\";\n"
                + "DROP TABLE \"main\".\"components\";\n"
                + "DROP TABLE \"main\".\"entities\";\n"
                + "DROP TABLE \"main\".\"entity_state_contents\";\n"
                + "DROP TABLE \"main\".\"entity_states\";\n"
                + "DROP TABLE \"main\".\"event_contents\";\n"
                + "DROP TABLE \"main\".\"event_parameters\";\n"
                + "DROP TABLE \"main\".\"event_targets_origins\";\n"
                + "DROP TABLE \"main\".\"events\";\n"
                + "DROP TABLE \"main\".\"instance_state_contents\";\n"
                + "DROP TABLE \"main\".\"instance_states\";\n"
                + "DROP TABLE \"main\".\"instances\";\n"
                + "DROP TABLE \"main\".\"possible_action_contents\";\n"
                + "DROP TABLE \"main\".\"possible_event_contents\";\n"
                + "DROP TABLE \"main\".\"possible_instance_contents\";\n"
                + "DROP TABLE \"main\".\"possible_entity_contents\";\n"
                + "DROP TABLE \"main\".\"conversations\";\n"
                + "DROP TABLE \"main\".\"messages\";\n"
                + "DROP TABLE \"main\".\"possible_interaction_contents\";\n"
                + "DROP TABLE \"main\".\"possible_agent_state_contents\";\n"
                + "DROP TABLE \"main\".\"interaction_contents\";\n"
                + "DROP TABLE \"main\".\"interaction_parameters\";\n"
                + "DROP TABLE \"main\".\"agent_state_contents\";\n"
                + "DROP TABLE \"main\".\"agent_states\";\n"
                + "DROP TABLE \"main\".\"interactions\";\n"
                + "DROP TABLE \"main\".\"reports\";\n"
                + "DROP TABLE \"main\".\"generated_events\";\n"
                + "DROP TABLE \"main\".\"generated_actions\";";
        stmt.executeUpdate(sql);
        stmt.close();
    }
}
