import { Pipe, PipeTransform } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'mapListCondition'
})
export class MapListConditionPipe implements PipeTransform {

  transform(row: any, tableData: any, validationCheck: any, ...args: any[]): any {
    if(validationCheck){
      let validation = false;
      if(!row.condition && row.condition !== 0){
        return true;
      } else {
        tableData.map((data, index) => {
          if(index == row.condition){
            if(row.conditionValue.includes(data.value)){
              validation = true;
            }
          }
        });
        return validation;
      }
    }
    else{
      if(tableData.filter(row => row.hideIfNull === true && row.value === null).length == tableData.length){
        return true;
      }
      else{
        return false;
      }
    }
  }

}
