import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  public getItem(key: string): any {
    try {
      const str = localStorage.getItem(key) ? localStorage.getItem(key) : 'de';
      return JSON.parse(String(str));
    } catch {
      this.removeItem(key);

      return null;
    }
  }

  public getTableState(key: string): { columns: any; filter: any; page: any; sort: any } {
    return this.getItem(key);
  }

  public removeItem(key: string): void {
    localStorage.removeItem(key);
  }

  public setItem(key: string, value: any): boolean {
    try {
      localStorage.setItem(key, JSON.stringify(value));

      return true;
    } catch {
      return false;
    }
  }

  public setTableState(key: string, filter: any, sort: any, page: any, columns: any): void {
    const tableState = { filter, sort, page, columns };

    this.setItem(key, tableState);
  }
}
