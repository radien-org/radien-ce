import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {StorageService} from '../../../shared/services/storage/storage.service';
import {LOCAL} from '../../../shared/services/storage/local.enum';
import {PrimeNGConfig} from "primeng/api";
import {DataAcquisitionService} from '../../../shared/services/data-acquisition/data-acquisition.service';

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
                    label: this.translationService.instant('zurück'),
                    link: '/data-acquisition/full-body'//TODO we need put variation for work-accident
                },
                {
                    label: this.translationService.instant('weiter'),
                    link: '/data-acquisition/more-injuries'
                }
            ]
        }
    }

    bodyPart: string = '';
    bodyPartSimple: string = '';
    bodyPartImage: string = '';

    items = [];
    item: string = '';
    selectedOptions: string[] = [];
    options: any[];

    selectedSubOptions: string[] = [];
    subOptions: any[];

    titleBodyPart = '';

    constructor(
        private readonly translationService: TranslateService,
        private readonly storageService: StorageService,
        private primengConfig: PrimeNGConfig,
        private readonly dataService: DataAcquisitionService
    ) {
        this.options = [];
        this.subOptions = [];
    }

    ngOnInit(): void {
      this.selectBodyPart(this.storageService.getItem(LOCAL.BODY_PART));
    }

    getInjuriesOptions() {
        this.dataService.getInjuriesOptions().then((data: any) => {
            if (data) {
                data.forEach((item: any) => {
                    if (this.bodyPartSimple == item.bodyPart) {
                        this.options.push(
                            {name: item.text, code: item.id}
                        );
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
                    this.subOptions.push(
                        {name: item.text, code: item.id}
                    );
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
                this.titleBodyPart = 'head';
                this.bodyPartSimple = 'head';
                break;
            case 'elbow-left':
                result = 'ProTec_Software_Bodyparts_Elbow-left.svg';
                this.titleBodyPart = 'elbow';
                this.bodyPartSimple = 'elbow';
                break;
            case 'upper-arm-left':
                result = 'ProTec_Software_Bodyparts_Forearm-left.svg';
                this.bodyPartSimple = 'arm';
                break;
            case 'shoulder-left':
                result = 'ProTec_Software_Bodyparts_Shoulder-left.svg';
                this.bodyPartSimple = 'shoulder';
                break;
            case 'torso':
                result = 'ProTec_Software_Bodyparts_Torso.svg';
                this.bodyPartSimple = 'torso';
                break;
            case 'shoulder-right':
                result = 'ProTec_Software_Bodyparts_Shoulder-right.svg';
                this.bodyPartSimple = 'shoulder';
                break;
            case 'upper-arm-right':
                result = 'ProTec_Software_Bodyparts_Bicep-left.svg';
                this.bodyPartSimple = 'arm';
                break;
            case 'elbow-right':
                result = 'ProTec_Software_Bodyparts_Elbow-right.svg';
                this.bodyPartSimple = 'elbow';
                break;
            case 'hand-left':
                result = 'ProTec_Software_Bodyparts_Hand-left.svg';
                this.bodyPartSimple = 'hand';
                break;
            case 'forearm-left':
                result = 'ProTec_Software_Bodyparts_Forearm-left.svg';
                this.bodyPartSimple = 'forearm';
                break;
            case 'hip-left':
                result = 'ProTec_Software_Bodyparts_Pelvis-left.svg';
                this.bodyPartSimple = 'hip';
                break;
            case 'hip-right':
                result = 'ProTec_Software_Bodyparts_Pelvis-right.svg';
                this.bodyPartSimple = 'hip';
                break;
            case 'forearm-right':
                result = 'ProTec_Software_Bodyparts_Forearm-right.svg';
                this.bodyPartSimple = 'forearm';
                break;
            case 'hand-right':
                result = 'ProTec_Software_Bodyparts_Hand-right.svg';
                this.bodyPartSimple = 'hand';
                break;
            case 'thigh-left':
                result = 'ProTec_Software_Bodyparts_Thigh-left.svg';
                this.bodyPartSimple = 'thigh';
                break;
            case 'thigh-right':
                result = 'ProTec_Software_Bodyparts_Thigh-right.svg';
                this.bodyPartSimple = 'thigh';
                break;
            case 'knee-left':
                result = 'ProTec_Software_Bodyparts_Knee-left.svg';
                this.bodyPartSimple = 'knee';
                break;
            case 'knee-right':
                result = 'ProTec_Software_Bodyparts-Knee-right.svg';
                this.bodyPartSimple = 'knee';
                break;
            case 'leg-left':
                result = 'ProTec_Software_Bodyparts_Tibia-left.svg';
                this.bodyPartSimple = 'leg';
                break;
            case 'leg-right':
                result = 'ProTec_Software_Bodyparts_Tibia-right.svg';
                this.bodyPartSimple = 'leg';
                break;
            case 'foot-left':
                result = 'ProTec_Software_Bodyparts_Foot-left.svg';
                this.bodyPartSimple = 'foot';
                break;
            case 'foot-right':
                result = 'ProTec_Software_Bodyparts_Foot-right.svg';
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
