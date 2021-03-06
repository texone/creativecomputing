/*
 *************************************************************************
 *
 *  IESNA.H - IESNA LM-63 Photometric Data Module Include File
 *
 *  Version:    1.00D
 *
 *  History:    95/08/15 - Created.
 *              95/08/29 - Version 1.00A release.
 *              95/09/01 - Revised IE_MaxLabel and IE_MaxLine definitions.
 *              95/09/04 - Version 1.00B release.
 *              96/01/28 - Added CIE luminaire type classification
 *                         definitions.
 *                       - Added calculated photometric data definitions.
 *                       - Added IE_Calc and IE_Zonal data structures.
 *                       - Added IE_CalcData and IE_CalcCU_Array function
 *                         prototypes.
 *                       - Added coefficients of utilization array
 *                         dimensions.
 *                       - Added IE_CU_Array and IE_CIE_Type
 *                         declarations.
 *                       - Added definition for pi.
 *              96/01/30 - Version 1.00C release.
 *              98/03/09 - Version 1.00D release.
 *
 *  Compilers:  Any ANSI C-compliant compiler
 *
 *  Author:     Ian Ashdown, P. Eng.
 *              byHeart Consultants Limited
 *              620 Ballantree Road
 *              West Vancouver, B.C.
 *              Canada V7S 1W3
 *              Tel. (604) 922-6148
 *              Fax. (604) 987-7621
 *
 *  Copyright 1995-1998 byHeart Consultants Limited
 *
 *  Permission: The following source code is copyrighted. However, it may
 *              be freely copied, redistributed, and modified for personal
 *              use or for royalty-free inclusion in commercial programs.
 *
 *************************************************************************
 */

#ifndef _IESNA_H
#define _IESNA_H

#include <stdio.h>

#define FALSE   0
#define TRUE    1

#define PI      3.141592654






typedef int BOOL;               /* Boolean flag                         */

typedef struct IE_Data          /* IESNA Standard File data             */
{
 

  struct IE_Label               /* Label line linked list element       */
  {
    char *line;                 /* Label line pointer                   */
    struct IE_Label *pnext;     /* Next list element pointer            */
  }
  *plline;                      /* Label line linked list pointer       */


  
}
IE_DATA;



extern const char *IE_CIE_Type[5];

extern BOOL IE_ReadFile( char *, IE_DATA * );
extern BOOL IE_CalcData( IE_DATA *, IE_CALC * );
extern void IE_Flush( IE_DATA * );

#endif

