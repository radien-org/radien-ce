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

    pageNav = {
        navegation: {
            type: 'navegation-buttons',
            navegations: [
                {
                    label: this.translationService.instant('ZURÜCK'),
                    link: '/data-acquisition/full-body'//TODO we need put variation for work-accident
                },
                {
                    label: this.translationService.instant('WEITER'),
                    link: '/data-acquisition/more-injuries'
                }
            ]
        }
    }

    bodyPart: string = '';
    bodyPartTitle: string = '';
    bodyPartSimple: string = '';
    bodyPartImage: string = '';

    primaryOptions : {
        id: 'primary_multi_select',
        title: string,
        options: any[],
    }

    secondaryOptions : {
        id: 'secondary_multi_select',
        title: string,
        options: any[],
    }

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
            title: 'Verletzung(en)',
            options: [],
        }
        this.secondaryOptions = {
            id: "secondary_multi_select",
            title: 'Verletzungsfolge(n)',
            options: [],
        }
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

    ngOnInit(): void {
      this.selectBodyPart(this.storageService.getItem(LOCAL.BODY_PART));
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
                this.bodyPartTitle = 'left elbow'
                this.bodyPartSimple = 'elbow';
                break;
            case 'shoulder-left':
                result = 'ProTec_Software_Bodyparts_Shoulder-left.svg';
                this.bodyPartTitle = 'left shoulder'
                this.bodyPartSimple = 'shoulder';
                break;
            case 'torso':
                result = 'ProTec_Software_Bodyparts_Torso.svg';
                this.bodyPartTitle = 'torso'
                this.bodyPartSimple = 'chest';
                break;
            case 'shoulder-right':
                result = 'ProTec_Software_Bodyparts_Shoulder-right.svg';
                this.bodyPartTitle = 'right shoulder'
                this.bodyPartSimple = 'shoulder';
                break;
            case 'upper-arm-right':
                result = 'ProTec_Software_Bodyparts_Bicep-right.svg';
                this.bodyPartTitle = 'right bicep'
                this.bodyPartSimple = 'arm';
                break;
            case 'upper-arm-left':
                result = 'ProTec_Software_Bodyparts_Bicep-left.svg';
                this.bodyPartTitle = 'left bicep'
                this.bodyPartSimple = 'arm';
                break;
            case 'elbow-right':
                result = 'ProTec_Software_Bodyparts_Elbow-right.svg';
                this.bodyPartTitle = 'right elbow'
                this.bodyPartSimple = 'elbow';
                break;
            case 'hand-left':
                result = 'ProTec_Software_Bodyparts_Hand-left.svg';
                this.bodyPartTitle = 'left hand'
                this.bodyPartSimple = 'hand';
                break;
            case 'forearm-left':
                result = 'ProTec_Software_Bodyparts_Forearm-left.svg';
                this.bodyPartTitle = 'left forearm'
                this.bodyPartSimple = 'forearm';
                break;
            case 'hip-left':
                result = 'ProTec_Software_Bodyparts_Pelvis-left.svg';
                this.bodyPartTitle = 'left pelvis'
                this.bodyPartSimple = 'hip';
                break;
            case 'hip-right':
                result = 'ProTec_Software_Bodyparts_Pelvis-right.svg';
                this.bodyPartTitle = 'right pelvis'
                this.bodyPartSimple = 'hip';
                break;
            case 'forearm-right':
                result = 'ProTec_Software_Bodyparts_Forearm-right.svg';
                this.bodyPartTitle = 'right forearm'
                this.bodyPartSimple = 'forearm';
                break;
            case 'hand-right':
                result = 'ProTec_Software_Bodyparts_Hand-right.svg';
                this.bodyPartTitle = 'right hand'
                this.bodyPartSimple = 'hand';
                break;
            case 'thigh-left':
                result = 'ProTec_Software_Bodyparts_Thigh-left.svg';
                this.bodyPartTitle = 'left tight'
                this.bodyPartSimple = 'tight';
                break;
            case 'thigh-right':
                result = 'ProTec_Software_Bodyparts_Thigh-right.svg';
                this.bodyPartTitle = 'right tight'
                this.bodyPartSimple = 'tight';
                break;
            case 'knee-left':
                result = 'ProTec_Software_Bodyparts_Knee-left.svg';
                this.bodyPartTitle = 'left knee'
                this.bodyPartSimple = 'knee';
                break;
            case 'knee-right':
                result = 'ProTec_Software_Bodyparts-Knee-right.svg';
                this.bodyPartTitle = 'right knee'
                this.bodyPartSimple = 'knee';
                break;
            case 'leg-left':
                result = 'ProTec_Software_Bodyparts_Tibia-left.svg';
                this.bodyPartTitle = 'left tibia'
                this.bodyPartSimple = 'tibia';
                break;
            case 'leg-right':
                result = 'ProTec_Software_Bodyparts_Tibia-right.svg';
                this.bodyPartTitle = 'right tibia'
                this.bodyPartSimple = 'tibia';
                break;
            case 'foot-left':
                result = 'ProTec_Software_Bodyparts_Foot-left.svg';
                this.bodyPartTitle = 'left foot'
                this.bodyPartSimple = 'foot';
                break;
            case 'foot-right':
                result = 'ProTec_Software_Bodyparts_Foot-right.svg';
                this.bodyPartTitle = 'right foot'
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
