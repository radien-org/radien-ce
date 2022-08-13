import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

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

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService) { }

  ngOnInit(): void {}

  getBodyPartImage() {
    let result = '';
    this.bodyPart = this.storageService.getItem(LOCAL.BODY_PART);
    switch (this.bodyPart) {
      case 'head':
        result = 'ProTec_Software_Figure_pa-head.svg';
        break;
      case 'elbow-left':
        result = 'ProTec_Software_Figure_pa-elbow-left.svg';
        break;    
      case 'upper-arm-left':
        result = 'ProTec_Software_Figure_pa-upper-arm-left.svg';
        break;    
      case 'shoulder-left':
        result = 'ProTec_Software_Figure_pa-shoulder-left.svg';
        break;    
      case 'torso':
        result = 'ProTec_Software_Figure_pa-torso.svg';
        break;    
      case 'shoulder-right':
        result = 'ProTec_Software_Figure_pa-shoulder-right.svg';
        break;    
      case 'upper-arm-right':
        result = 'ProTec_Software_Figure_pa-upper-arm-right.svg';
        break;    
      case 'elbow-right':
        result = 'ProTec_Software_Figure_pa-elbow-right.svg';
        break;    
      case 'hand-left':
        result = 'ProTec_Software_Figure_pa-hand-left.svg';
        break;    
      case 'forearm-left':
        result = 'ProTec_Software_Figure_pa-forearm-left.svg';
        break;    
      case 'hip-left':
        result = 'ProTec_Software_Figure_pa-hip-left.svg';
        break;    
      case 'hip-right':
        result = 'ProTec_Software_Figure_pa-hip-right.svg';
        break;    
      case 'forearm-right':
        result = 'ProTec_Software_Figure_pa-forearm-right.svg';
        break;    
      case 'hand-right':
        result = 'ProTec_Software_Figure_pa-hand-right.svg';
        break;    
      case 'thigh-left':
        result = 'ProTec_Software_Figure_pa-thigh-left.svg';
        break;    
      case 'thigh-right':
        result = 'ProTec_Software_Figure_pa-thigh-right.svg';
        break;    
      case 'knee-left':
        result = 'ProTec_Software_Figure_pa-knee-left.svg';
        break;    
      case 'knee-right':
        result = 'ProTec_Software_Figure_pa-knee-right.svg';
        break;    
      case 'leg-left':
        result = 'ProTec_Software_Figure_pa-lower-leg-left.svg';
        break;    
      case 'leg-right':
        result = 'ProTec_Software_Figure_pa-lower-leg-right.svg';
        break;    
      case 'foot-left':
        result = 'ProTec_Software_Figure_pa-foot-left.svg';
        break;    
      case 'foot-right':
        result = 'ProTec_Software_Figure_pa-foot-right.svg';
        break;   
      default:
        result = 'ProTec_Software_Figure_pixel-aigned_big.svg';
        break;
    }
    return '../../../../assets/images/body/' + result;
  }

}
