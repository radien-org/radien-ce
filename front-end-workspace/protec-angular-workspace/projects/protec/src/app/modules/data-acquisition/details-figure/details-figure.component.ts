import { Component, ViewChild, ElementRef, AfterViewInit, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-details-figure',//TODO change this component name to full-body-figure
  templateUrl: './details-figure.component.html',
  styleUrls: ['./details-figure.component.scss']
})
export class DetailsFigureComponent implements OnInit {

  //@ViewChild('bodyCanvas')
  //private bodyCanvas: ElementRef = {} as ElementRef;
  //context = CanvasRenderingContext2D;

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/'
        }
      ]
    }
  }

  accidentType: string = '';

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService) {}

  ngAfterViewInit(): void {
    //this.context = this.bodyCanvas.nativeElement.getContext('2d');
    this.buildCanvas();
  }

  ngOnInit(): void {
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    this.verifyAccidentType();
  }

  buildCanvas() {

    let canvas: any = document.getElementById('bodyCanvas');
    if (canvas!.getContext) {
      let context = canvas.getContext('2d');
      let base_image = new Image();
      base_image.src = '/assets/images/body/ProTec_Software_Figure_pa-head.svg';
      //this.context.drawImage(base_image, 100, 100);
      //context.drawImage(base_image, 100, 100);
      context.clearRect(0, 0, canvas.width, canvas.height);
      base_image.onload = function(){
        const w = base_image.width, h = base_image.height;
        context.drawImage(base_image, 0, 0, 30, 30);
        //context.drawImage(base_image, 0, 0, 250, 150);
        //context.imageSmoothingEnabled = true;
        //context.drawImage(base_image, 0, 0, w * 2, h * 2);
        //context.drawImage(base_image, 0, 0, 250, 150);
      }
    }

  }

  verifyAccidentType() {
    this.pageNav.navegation.navegations.splice(0, 1);
    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/illness-details'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/part-body'
        }
      );
    } else if(this.accidentType === 'disease') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/work-accident'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/part-body'
        }
      );
    } else {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/work-accident'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/part-body'
        }
      );
    }
  }

}
