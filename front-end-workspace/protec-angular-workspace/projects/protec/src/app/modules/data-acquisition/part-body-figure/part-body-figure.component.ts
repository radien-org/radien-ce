import {Component, OnInit, ViewChild} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {StorageService} from '../../../shared/services/storage/storage.service';
import {LOCAL} from '../../../shared/services/storage/local.enum';
import {PrimeNGConfig} from "primeng/api";
import {DataAcquisitionService} from '../../../shared/services/data-acquisition/data-acquisition.service';
import {MultiSelectComponent} from "../../../shared/components/multi-select/multi-select.component";

@Component({
    selector: 'app-part-body-figure',
    templateUrl: './part-body-figure.component.html',
    styleUrls: ['./part-body-figure.component.scss']
})
export class PartBodyFigureComponent implements OnInit {

    initNavigation = [{
        label: this.translationService.instant('zurück'),
        runPreNavHook: true,
        link: '/data-acquisition/full-body'//TODO we need put variation for work-accident
    }]

    pageNav = {
        navegation: {
            type: 'navegation-buttons',
            navegations: [...this.initNavigation]
        }
    }

    bodyPart: string = '';
    bodyPartTitle: string = '';
    bodyPartSide: string = '';
    bodyPartSimple: string = '';
    bodyPartImage: string = '';

    primaryOptions : {
        id: 'primary_multi_select',
        title: string,
        options: any[],
        error: ''
    }

    secondaryOptions : {
        id: 'secondary_multi_select',
        title: string,
        options: any[],
        error: ''
    }
    selectedOptions: Set<any> = new Set<any>();
    selectedSubOptions: Set<any> = new Set<any>();

    @ViewChild('primary_multi_select') selectPrimaryComp: MultiSelectComponent | undefined;
    @ViewChild('secondary_multi_select') selectSecondaryComp: MultiSelectComponent | undefined;

    items = [];
    item: string = '';


    constructor(
        private readonly translationService: TranslateService,
        private readonly storageService: StorageService,
        private primengConfig: PrimeNGConfig,
        private readonly dataService: DataAcquisitionService
    ) {
        this.primaryOptions = {
            id: 'primary_multi_select',
            title: 'primary_accident_options',
            options: [],
            error: ''
        }
        this.secondaryOptions = {
            id: "secondary_multi_select",
            title: 'secondary_accident_options',
            options: [],
            error: ''
        }
    }
    ngOnInit(): void {
        this.selectBodyPart(this.storageService.getItem(LOCAL.BODY_PART));
        try {
            this.selectedOptions = new Set(this.storageService.getItem(LOCAL.PART_BODY_FIGURE_FORM).selectedOptions) || new Set<any>();
            this.primaryOptions.error = ''
            this.selectedSubOptions = new Set(this.storageService.getItem(LOCAL.PART_BODY_FIGURE_FORM).selectedSubOptions) || new Set<any>();
            this.secondaryOptions.error = ''
        } catch (err) {
            this.selectedOptions = new Set<any>();
            this.selectedSubOptions = new Set<any>();
        }
        this.verifyInput();
    }

    handleOpen(id: string) {
        if (id === 'primary_multi_select' ) {
            console.log(id)
            console.log(this.selectSecondaryComp)
            if (this.selectSecondaryComp) this.selectSecondaryComp.toggleOff()
        } else {
            if (this.selectPrimaryComp) this.selectPrimaryComp.toggleOff()
        }
    }

   handleSelectOptions(options: Set<any>): void {
        this.selectedOptions = options;
        this.verifyInput();
   }
   handleSelectSubOptions(options: Set<any>) : void {
        this.selectedSubOptions = options;
        this.verifyInput();
   }

    verifyInput(): void {
        if (this.selectedOptions.size > 0 && this.selectedSubOptions.size > 0) {
            this.pageNav.navegation.navegations = [...this.initNavigation, {
                label: this.translationService.instant('weiter'),
                link: '/data-acquisition/more-injuries',
                runPreNavHook: false,
            }]
            this.saveInputs();
        } else {
            this.pageNav.navegation.navegations = [...this.initNavigation]
        }
    }
    saveInputs(): void {
        this.storageService.setItem(LOCAL.PART_BODY_FIGURE_FORM, {selectedOptions: Array.from(this.selectedOptions), selectedSubOptions: Array.from(this.selectedSubOptions) })
    }

    clearSavedData() : void {
        console.log('called pre nav hook')
        this.storageService.setItem(LOCAL.PART_BODY_FIGURE_FORM, {selectedOptions: [], selectedSubOptions: [] })
    }


    getInjuriesOptions() {
        this.dataService.getInjuriesOptions().then((data: any) => {
            if (data) {
                data.forEach((item: any) => {
                    if (this.bodyPartSimple == item.bodyPart) {
                        this.primaryOptions.options.push( {name: item.text, code: item.id})
                    }
                });
            }
            this.getConsequencesOptions();
        });
    }

    getConsequencesOptions() {
        this.dataService.getConsequencesOptions().then((data: any) => {
            if (data) {
                data.forEach((item: any) => {
                    this.secondaryOptions.options.push( {name: item.text, code: item.id})
                });
            }
        });
    }

    selectNoBtn(data: any) {
        if (data) {

        }
    }

    selectBodyPart(bodyPart: string) {
        let result = '';
        this.bodyPart = bodyPart;
        switch (this.bodyPart) {
            case 'head':
                result = 'ProTec_Software_Bodyparts_Head.svg';
                this.bodyPartTitle = 'head'
                this.bodyPartSimple = 'head';
                break;
            case 'elbow-left':
                result = 'ProTec_Software_Bodyparts_Elbow-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'elbow';
                this.bodyPartSimple = 'elbow';
                break;
            case 'shoulder-left':
                result = 'ProTec_Software_Bodyparts_Shoulder-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'shoulder'
                this.bodyPartSimple = 'shoulder';
                break;
            case 'torso':
                result = 'ProTec_Software_Bodyparts_Torso.svg';
                this.bodyPartTitle = 'chest'
                this.bodyPartSimple = 'chest';
                break;
            case 'shoulder-right':
                result = 'ProTec_Software_Bodyparts_Shoulder-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'shoulder'
                this.bodyPartSimple = 'shoulder';
                break;
            case 'upper-arm-right':
                result = 'ProTec_Software_Bodyparts_Bicep-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'arm'
                this.bodyPartSimple = 'arm';
                break;
            case 'upper-arm-left':
                result = 'ProTec_Software_Bodyparts_Bicep-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'arm'
                this.bodyPartSimple = 'arm';
                break;
            case 'elbow-right':
                result = 'ProTec_Software_Bodyparts_Elbow-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'elbow'
                this.bodyPartSimple = 'elbow';
                break;
            case 'hand-left':
                result = 'ProTec_Software_Bodyparts_Hand-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'hand'
                this.bodyPartSimple = 'hand';
                break;
            case 'forearm-left':
                result = 'ProTec_Software_Bodyparts_Forearm-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'forearm'
                this.bodyPartSimple = 'forearm';
                break;
            case 'hip-left':
                result = 'ProTec_Software_Bodyparts_Pelvis-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'hip'
                this.bodyPartSimple = 'hip';
                break;
            case 'hip-right':
                result = 'ProTec_Software_Bodyparts_Pelvis-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'hip'
                this.bodyPartSimple = 'hip';
                break;
            case 'forearm-right':
                result = 'ProTec_Software_Bodyparts_Forearm-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'forearm'
                this.bodyPartSimple = 'forearm';
                break;
            case 'hand-right':
                result = 'ProTec_Software_Bodyparts_Hand-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'hand'
                this.bodyPartSimple = 'hand';
                break;
            case 'thigh-left':
                result = 'ProTec_Software_Bodyparts_Thigh-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'tight'
                this.bodyPartSimple = 'tight';
                break;
            case 'thigh-right':
                result = 'ProTec_Software_Bodyparts_Thigh-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'tight'
                this.bodyPartSimple = 'tight';
                break;
            case 'knee-left':
                result = 'ProTec_Software_Bodyparts_Knee-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'knee'
                this.bodyPartSimple = 'knee';
                break;
            case 'knee-right':
                result = 'ProTec_Software_Bodyparts-Knee-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'knee'
                this.bodyPartSimple = 'knee';
                break;
            case 'leg-left':
                result = 'ProTec_Software_Bodyparts_Tibia-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'tibia'
                this.bodyPartSimple = 'tibia';
                break;
            case 'leg-right':
                result = 'ProTec_Software_Bodyparts_Tibia-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'tibia'
                this.bodyPartSimple = 'tibia';
                break;
            case 'foot-left':
                result = 'ProTec_Software_Bodyparts_Foot-left.svg';
                this.bodyPartSide = 'left';
                this.bodyPartTitle = 'foot'
                this.bodyPartSimple = 'foot';
                break;
            case 'foot-right':
                result = 'ProTec_Software_Bodyparts_Foot-right.svg';
                this.bodyPartSide = 'right';
                this.bodyPartTitle = 'foot'
                this.bodyPartSimple = 'foot';
                break;
            default:
                result = 'ProTec_Software_Figure_pixel-aigned_big.svg';
                this.bodyPartSimple = '';
                break;
        }
        this.bodyPartImage = '../../../../assets/images/body-2/' + result;
        this.getInjuriesOptions();
    }

    getBodyPartImage() {
        return this.bodyPartImage;
    }

}
